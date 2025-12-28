package com.argus.utilities.benchmark;

import com.argus.utilities.artifact_fetcher.MavenCoordinate;
import org.junit.jupiter.api.Test;

class BenchmarkIntegrationTest {

    @Test
    void runBenchmark() throws Exception {
        // Compare slf4j-api 1.7.35 vs 1.7.36
        // This is a safe smoke test as these artifacts are small and public
        System.out.println("Running Benchmark...");
        new BenchmarkRunner().run(
                new MavenCoordinate("org.slf4j", "slf4j-api", "1.7.35"),
                new MavenCoordinate("org.slf4j", "slf4j-api", "1.7.36"));
    }
}
