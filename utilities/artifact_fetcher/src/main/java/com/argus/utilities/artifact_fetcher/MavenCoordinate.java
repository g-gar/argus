package com.argus.utilities.artifact_fetcher;

public record MavenCoordinate(String groupId, String artifactId, String version) {
    public String toPath() {
        // org.springframework -> org/springframework
        return groupId.replace('.', '/') + "/" + artifactId + "/" + version;
    }

    public String getJarName() {
        return artifactId + "-" + version + ".jar";
    }
}
