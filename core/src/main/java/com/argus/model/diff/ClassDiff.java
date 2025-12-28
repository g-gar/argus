package com.argus.model.diff;

import com.argus.model.ClassInfo;
import lombok.Builder;

import java.util.List;
import java.util.Optional;

/**
 * Represents the differences found within a class between two versions.
 *
 * @param className         The fully qualified name of the class.
 * @param changeType        The overall type of change for the class.
 * @param oldClass          The class information from the old version (empty if
 *                          ADDED).
 * @param newClass          The class information from the new version (empty if
 *                          REMOVED).
 * @param methodDifferences List of differences in methods.
 * @param fieldDifferences  List of differences in fields.
 */
@Builder
public record ClassDiff(
        String className,
        ChangeType changeType,
        Optional<ClassInfo> oldClass,
        Optional<ClassInfo> newClass,
        List<MethodDiff> methodDifferences,
        List<FieldDiff> fieldDifferences) {
    /**
     * Calculates the number of breaking changes within this class.
     * <p>
     * Counts methods that are REMOVED or marked as BREAKING_CHANGE.
     * </p>
     *
     * @return The count of breaking changes.
     */
    public long getBreakingCount() {
        return methodDifferences.stream()
                .filter(m -> m.changeType() == ChangeType.BREAKING_CHANGE || m.changeType() == ChangeType.REMOVED)
                .count();
    }
}
