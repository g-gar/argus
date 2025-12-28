package com.argus.bat.asm;

import com.argus.model.ClassInfo;
import com.argus.model.LibraryVersion;
import org.junit.jupiter.api.Test;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AsmArtifactAnalyzerTest {

    @Test
    void analyzeCoreJar() throws Exception {
        System.out.println("CWD: " + new File(".").getAbsolutePath());
        File targetJar = new File("../../core/build/libs/core.jar");

        if (!targetJar.exists()) {
            fail("Target JAR not found: " + targetJar.getAbsolutePath());
        }

        AsmArtifactAnalyzer analyzer = new AsmArtifactAnalyzer();
        System.out.println("Analyzing: " + targetJar.getName());

        // Test canHandle with BufferedInputStream (supports mark/reset)
        try (InputStream input = new BufferedInputStream(new FileInputStream(targetJar))) {
            assertTrue(analyzer.canHandle(input));
        }

        // Test getJavaMajorVersion
        try (InputStream input = new FileInputStream(targetJar)) {
            int javaVersion = analyzer.getJavaMajorVersion(input);
            System.out.println("Java Version: " + javaVersion);
            assertTrue(javaVersion >= 61, "Should be at least Java 17");
        }

        // Test getArtifactMetadata
        try (InputStream input = new FileInputStream(targetJar)) {
            LibraryVersion metadata = analyzer.getArtifactMetadata(input);
            assertNotNull(metadata);
            System.out.println("Coordinates: " + metadata.coordinates());
        }

        // Test analyze
        try (InputStream input = new FileInputStream(targetJar)) {
            List<ClassInfo> classes = analyzer.analyze(input);
            assertFalse(classes.isEmpty(), "Should find classes in core jar");

            boolean foundLibraryVersion = classes.stream()
                    .anyMatch(c -> c.name().equals("com.argus.model.LibraryVersion"));
            assertTrue(foundLibraryVersion, "Should have found LibraryVersion class");

            for (ClassInfo cls : classes) {
                System.out.println(" - " + cls.name() + " (" + cls.methods().size() + " methods)");
            }
        }
    }
}
