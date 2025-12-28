package com.argus.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Set;

/**
 * Represents a method within a class.
 */
@Value
@Builder
@lombok.experimental.Accessors(fluent = true)
public class MethodInfo {

    /**
     * The name of the method.
     * <p>
     * Example: "hasText"
     * </p>
     */
    String name;

    /**
     * The raw ASM method descriptor.
     * <p>
     * Example: "(Ljava/lang/String;)Z"
     * </p>
     */
    String descriptor;

    /**
     * The readable return type of the method.
     * <p>
     * Example: "boolean"
     * </p>
     */
    String returnType;

    /**
     * A list of readable parameter types.
     * <p>
     * Example: ["java.lang.String"]
     * </p>
     */
    List<String> paramTypes;

    /**
     * Indicates if the method is static.
     */
    boolean isStatic;

    /**
     * Indicates if the method is public.
     */
    boolean isPublic;

    /**
     * Indicates if the method is deprecated.
     */
    boolean isDeprecated;

    /**
     * A set of annotations present on this method.
     * <p>
     * This captures metadata like @Deprecated, @Nullable, etc.
     * </p>
     */
    Set<AnnotationInfo> annotations;

    /**
     * Generates a unique signature for the method, useful for logs or frontend
     * identification.
     *
     * @return The unique signature combining name and descriptor.
     */
    public String getUniqueSignature() {
        return name + descriptor;
    }
}
