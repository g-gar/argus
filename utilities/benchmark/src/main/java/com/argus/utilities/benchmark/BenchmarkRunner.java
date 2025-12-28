package com.argus.utilities.benchmark;

import com.argus.bat.asm.AsmArtifactAnalyzer;
import com.argus.diff.LibraryComparator;
import com.argus.model.diff.LibraryDiff;
import com.argus.utilities.artifact_fetcher.MavenCoordinate;
import com.argus.utilities.artifact_fetcher.MavenFetcher;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.cmp.JApiCmpArchive;
import japicmp.model.JApiClass;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class BenchmarkRunner {

    private final MavenFetcher fetcher;
    private final AsmArtifactAnalyzer analyzer;
    private final LibraryComparator comparator;

    public BenchmarkRunner() {
        this.fetcher = new MavenFetcher();
        this.analyzer = new AsmArtifactAnalyzer();
        this.comparator = new LibraryComparator();
    }

    public void run(MavenCoordinate v1, MavenCoordinate v2) throws Exception {
        log.info("Benchmarking {} vs {}", v1, v2);

        // 1. Fetch JARs
        Path p1 = fetcher.fetchJar(v1);
        Path p2 = fetcher.fetchJar(v2);

        // 2. Run Argus
        long startArgus = System.currentTimeMillis();
        var classes1 = analyzer.analyze(new FileInputStream(p1.toFile()));
        var classes2 = analyzer.analyze(new FileInputStream(p2.toFile()));

        var lib1 = com.argus.model.LibraryVersion.builder()
                .version(v1.version())
                .classes(classes1)
                .build();
        var lib2 = com.argus.model.LibraryVersion.builder()
                .version(v2.version())
                .classes(classes2)
                .build();

        LibraryDiff argusDiff = comparator.compare(lib1, lib2);
        long endArgus = System.currentTimeMillis();

        log.info("--- Argus Report ---");
        log.info("Time: {} ms", (endArgus - startArgus));
        log.info("Breaking Changes: {}", argusDiff.getBreakingCount());

        // 3. Run japicmp
        long startJapi = System.currentTimeMillis();
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);

        List<JApiClass> jApiClasses = jarArchiveComparator.compare(
                new JApiCmpArchive(p1.toFile(), v1.version()),
                new JApiCmpArchive(p2.toFile(), v2.version()));
        long endJapi = System.currentTimeMillis();

        long japiBreaking = jApiClasses.stream()
                .filter(c -> !c.isBinaryCompatible())
                .count();

        // Markdown Report for GitHub Job Summary
        System.out.println("### 📊 Argus vs japicmp Benchmark");
        System.out.println("");
        System.out.println("| Metric | Argus 👁️ | japicmp 📏 |");
        System.out.println("| :--- | :---: | :---: |");
        System.out.printf("| **Execution Time** | %d ms | %d ms |%n", (endArgus - startArgus), (endJapi - startJapi));
        System.out.printf("| **Breaking Changes** | %d | %d |%n", argusDiff.getBreakingCount(), japiBreaking);
        System.out.println("");
        System.out.println("> *Benchmark run on: " + v1 + " vs " + v2 + "*");
    }

    public static void main(String[] args) throws Exception {
        // Read properties from -Dargus.bench.v1="g:a:v"
        String v1Str = System.getProperty("argus.bench.v1", "org.slf4j:slf4j-api:1.7.35");
        String v2Str = System.getProperty("argus.bench.v2", "org.slf4j:slf4j-api:1.7.36");

        new BenchmarkRunner().run(
                parseCoordinate(v1Str),
                parseCoordinate(v2Str));
    }

    private static MavenCoordinate parseCoordinate(String s) {
        String[] parts = s.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid coordinate format (group:artifact:version): " + s);
        }
        return new MavenCoordinate(parts[0], parts[1], parts[2]);
    }
}
