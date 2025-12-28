package com.argus.utilities.artifact_fetcher;

import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.*;

@Slf4j
public class MavenFetcher {

    private static final String MAVEN_CENTRAL_PATTERN = "https://repo1.maven.org/maven2/%s/%s";
    private final Path localRepository;
    private final HttpClient client;

    public MavenFetcher() {
        this.localRepository = Paths.get(System.getProperty("user.home"), ".m2", "repository");
        this.client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
    }

    public Path fetchJar(MavenCoordinate coord) throws IOException, InterruptedException {
        Path localPath = localRepository.resolve(coord.toPath()).resolve(coord.getJarName());

        if (Files.exists(localPath)) {
            log.info("Using local cache: {}", coord.getJarName());
            return localPath;
        }

        String downloadUrl = String.format(MAVEN_CENTRAL_PATTERN, coord.toPath(), coord.getJarName());
        log.info("Downloading artifact from: {}", downloadUrl);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(downloadUrl)).GET().build();
        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        if (response.statusCode() != 200) {
            throw new IOException(
                    String.format("Failed to download %s. Status code: %d", coord, response.statusCode()));
        }

        Files.createDirectories(localPath.getParent());
        Files.copy(response.body(), localPath, StandardCopyOption.REPLACE_EXISTING);

        return localPath;
    }
}
