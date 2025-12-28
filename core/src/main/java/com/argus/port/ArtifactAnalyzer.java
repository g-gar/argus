package com.argus.port;

import com.argus.model.ClassInfo;
import com.argus.model.LibraryVersion;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Port interface for analyzing a software artifact (JAR, WAR, etc.).
 * <p>
 * This interface defines the contract for analyzing an artifact stream and
 * producing a full {@link LibraryVersion} model.
 * </p>
 */
public interface ArtifactAnalyzer {

    /**
     * Checks if this analyzer can handle the given artifact stream.
     * <p>
     * <b>Note:</b> The provided input stream must support {@code mark()} and
     * {@code reset()}.
     * This method will peek at the initial bytes (magic number) to determine
     * support without consuming the stream.
     * </p>
     *
     * @param input The input stream to check.
     * @return true if the artifact format is supported (e.g., valid magic bytes),
     *         false otherwise.
     * @throws IOException              If an I/O error occurs.
     * @throws IllegalArgumentException If the input stream does not support
     *                                  mark/reset.
     */
    boolean canHandle(InputStream input) throws IOException;

    /**
     * Determines the Java major version used to compile the classes in the
     * artifact.
     *
     * @param input The artifact input stream to inspect.
     * @return The Java major version number.
     * @throws IOException              If an I/O error occurs.
     * @throws IllegalArgumentException If the artifact cannot be handled.
     */
    int getJavaMajorVersion(InputStream input) throws IOException;

    /**
     * Extracts basic metadata (coordinates, version) from the artifact without
     * performing a full class scan.
     *
     * @param input The artifact input stream to inspect.
     * @return A {@link LibraryVersion} containing metadata.
     * @throws IOException              If an I/O error occurs.
     * @throws IllegalArgumentException If the artifact cannot be handled.
     */
    LibraryVersion getArtifactMetadata(InputStream input) throws IOException;

    /**
     * Performs a full analysis of the classes contained in the artifact.
     *
     * @param input The artifact input stream to analyze.
     * @return A list of {@link ClassInfo} objects representing the classes found.
     * @throws IOException              If an I/O error occurs during analysis.
     * @throws IllegalArgumentException If the artifact cannot be handled.
     */
    List<ClassInfo> analyze(InputStream input) throws IOException;
}
