package com.argus.model.diff;

import lombok.Builder;

/**
 * Represents a difference in a field between two versions.
 *
 * @param name       The name of the field.
 * @param changeType The type of change (e.g., ADDED, REMOVED, MODIFIED).
 */
@Builder
public record FieldDiff(
                String name,
                ChangeType changeType) {
}
