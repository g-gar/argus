package com.argus.model.diff;

import com.argus.model.MethodInfo;
import lombok.Builder;

import java.util.Optional;

/**
 * Represents a difference in a method between two versions.
 *
 * @param uniqueSignature The unique signature of the method (name +
 *                        descriptor).
 * @param changeType      The type of change (e.g., ADDED, REMOVED,
 *                        BREAKING_CHANGE).
 * @param oldMethod       The method information from the old version (empty if
 *                        ADDED).
 * @param newMethod       The method information from the new version (empty if
 *                        REMOVED).
 * @param description     A human-readable description of the change (e.g.,
 *                        "Return type changed").
 */
@Builder
public record MethodDiff(
                String uniqueSignature,
                ChangeType changeType,
                Optional<MethodInfo> oldMethod,
                Optional<MethodInfo> newMethod,
                String description) {
}
