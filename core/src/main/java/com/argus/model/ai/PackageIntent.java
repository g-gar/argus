package com.argus.model.ai;

/**
 * Represents the inferred intent behind changes in a specific package.
 *
 * @param packageName    The name of the package.
 * @param intentCategory The category of the intent (e.g., "Reactive Migration",
 *                       "Security Hardening").
 * @param explanation    A human-readable explanation of why this intent was
 *                       inferred.
 * @param impact         The assessed impact level of these changes.
 */
public record PackageIntent(
        String packageName,
        String intentCategory,
        String explanation,
        ImpactLevel impact) {
}
