package com.argus.model;

import lombok.Builder;
import lombok.Value;

/**
 * Represents a field within a class.
 * <p>
 * This includes static variables and constants which are considered part of the
 * API.
 * </p>
 */
@Value
@Builder
@lombok.experimental.Accessors(fluent = true)
public class FieldInfo {

    /**
     * The name of the field.
     */
    String name;

    /**
     * The type descriptor or readable type name of the field.
     */
    String type;

    /**
     * Indicates if the field is final.
     */
    boolean isFinal;

    /**
     * Indicates if the field is public.
     */
    boolean isPublic;

    /**
     * Indicates if the field is protected.
     */
    boolean isProtected;

    /**
     * Indicates if the field is static.
     */
    boolean isStatic;
}
