package com.argus.model;

import lombok.Builder;
import lombok.Value;
import java.util.Map;

/**
 * Represents metadata annotations found on a class, method, or field.
 * <p>
 * This class serves as a partial substitute for Javadoc in the bytecode,
 * capturing runtime and compile-time annotation data.
 * </p>
 */
@Value
@Builder
@lombok.experimental.Accessors(fluent = true)
public class AnnotationInfo {

    /**
     * The fully qualified name of the annotation.
     * <p>
     * Example: "java.lang.Deprecated"
     * </p>
     */
    String name;

    /**
     * A map of annotation element values.
     * <p>
     * Keys are the element names (e.g., "since", "forRemoval").
     * Values are the corresponding values (e.g., "5.2", true).
     * </p>
     */
    Map<String, Object> values;
}
