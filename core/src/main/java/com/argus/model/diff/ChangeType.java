package com.argus.model.diff;

/**
 * Enumerates the possible types of changes between two versions of an artifact
 * component.
 */
public enum ChangeType {
    /**
     * The component did not exist in the old version but exists in the new version.
     */
    ADDED,

    /**
     * The component existed in the old version but does not exist in the new
     * version.
     */
    REMOVED,

    /**
     * The component exists in both versions but has internal changes (e.g., body
     * changes, children changes).
     */
    MODIFIED,

    /**
     * The component exists in both versions but has incompatible changes (e.g.,
     * signature change, visibility reduction).
     */
    BREAKING_CHANGE,

    /**
     * The component is identical in both versions.
     */
    UNCHANGED
}
