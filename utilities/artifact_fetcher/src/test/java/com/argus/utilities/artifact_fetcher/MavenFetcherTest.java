package com.argus.utilities.artifact_fetcher;

import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MavenFetcherTest {

    @Test
    void fetchSlf4j() throws Exception {
        MavenFetcher fetcher = new MavenFetcher();
        MavenCoordinate slf4j = new MavenCoordinate("org.slf4j", "slf4j-api", "1.7.36");

        Path jarPath = fetcher.fetchJar(slf4j);

        assertNotNull(jarPath);
        assertTrue(Files.exists(jarPath));
        assertTrue(Files.size(jarPath) > 0);
        assertTrue(jarPath.toString().endsWith(".jar"));

        System.out.println("Downloaded to: " + jarPath);
    }
}
