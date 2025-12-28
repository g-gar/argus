package com.argus.model;

import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Set;

/**
 * Represents a class or interface analyzed from the bytecode.
 */
@Value
@Builder
@lombok.experimental.Accessors(fluent = true)
public class ClassInfo {

    /**
     * The fully qualified name of the class.
     * <p>
     * Example: "org.springframework.util.StringUtils"
     * </p>
     */
    String name;

    /**
     * The fully qualified name of the superclass.
     * <p>
     * Example: "java.lang.Object"
     * </p>
     */
    String superClassName;

    /**
     * A list of interfaces implemented by this class.
     */
    List<String> interfaces;

    /**
     * Indicates if this represents an interface.
     */
    boolean isInterface;

    /**
     * Indicates if the class is abstract.
     */
    boolean isAbstract;

    /**
     * Indicates if the class is deprecated.
     * <p>
     * This can be determined via a binary flag or annotation.
     * </p>
     */
    boolean isDeprecated;

    /**
     * A set of key metadata annotations on the class.
     */
    Set<AnnotationInfo> annotations;

    /**
     * A list of methods declared in this class.
     */
    List<MethodInfo> methods;

    /**
     * A list of fields declared in this class.
     */
    List<FieldInfo> fields;
}
