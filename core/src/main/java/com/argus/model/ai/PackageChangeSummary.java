package com.argus.model.ai;

import java.util.List;
import java.util.Set;

/**
 * Intermediate structure to feed the LLM with package change summaries.
 *
 * @param packageName        The name of the package.
 * @param totalChanges       The total count of changes in this package.
 * @param breakingSignatures Representative examples of breaking signatures.
 * @param newClasses         List of newly added classes.
 * @param removedClasses     List of removed classes.
 * @param affectedConcepts   Keywords representing affected concepts (e.g.,
 *                           "Security", "JPA").
 */
public record PackageChangeSummary(
        String packageName,
        int totalChanges,
        List<String> breakingSignatures,
        List<String> newClasses,
        List<String> removedClasses,
        Set<String> affectedConcepts) {
}
