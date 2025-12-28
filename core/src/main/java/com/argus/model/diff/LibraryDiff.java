package com.argus.model.diff;

import com.argus.model.LibraryVersion;
import lombok.Builder;

import java.util.List;

/**
 * Represents the result of a comparison between two library versions.
 *
 * @param oldVersion       The baseline library version.
 * @param newVersion       The target library version.
 * @param classDifferences List of differences found in classes.
 */
@Builder
public record LibraryDiff(
        LibraryVersion oldVersion,
        LibraryVersion newVersion,
        List<ClassDiff> classDifferences) {
    /**
     * Calculates the total number of breaking changes across the entire library.
     *
     * @return The sum of breaking changes from all class differences.
     */
    public long getBreakingCount() {
        return getBreakingCount(true);
    }

    public long getBreakingCount(boolean includeProtected) {
        return classDifferences.stream()
                .mapToLong(c -> c.getBreakingCount(includeProtected))
                .sum();
    }

    public long getCompactBreakingCount() {
        return classDifferences.stream()
                .mapToLong(ClassDiff::getCompactBreakingCount)
                .sum();
    }
}
