package com.argus.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;

/**
 * Represents the root of an analyzed JAR library.
 */
@Value
@Builder
@lombok.experimental.Accessors(fluent = true)
public class LibraryVersion {

    /**
     * The Maven coordinates of the library.
     * <p>
     * Example: "org.springframework:spring-core:6.0.0"
     * </p>
     */
    String coordinates;

    /**
     * The version of the library.
     * <p>
     * Example: "6.0.0"
     * </p>
     */
    String version;

    /**
     * The major Java version required by this library.
     * <p>
     * Example: 61 (Java 17)
     * </p>
     */
    int javaMajorVersion;

    /**
     * The list of classes contained in this library version.
     */
    List<ClassInfo> classes;
}
