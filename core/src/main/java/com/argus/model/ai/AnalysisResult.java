package com.argus.model.ai;

import java.util.List;

/**
 * The result of an AI-powered intent analysis on a library diff.
 *
 * @param overallSummary A high-level summary of the entire library evolution.
 * @param packageIntents A list of inferred intents for individual packages.
 */
public record AnalysisResult(
        String overallSummary,
        List<PackageIntent> packageIntents) {
}
