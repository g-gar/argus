package com.argus.service;

import com.argus.model.ai.AnalysisResult;
import com.argus.model.ai.PackageChangeSummary;
import com.argus.model.diff.LibraryDiff;
import com.argus.port.NeuralService;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * Service responsible for orchestrating the AI-powered intent analysis.
 * <p>
 * This service acts as the semantic layer:
 * <ol>
 * <li>It uses {@link ChangeClusterer} to reduce the raw diff into digestible
 * metrics.</li>
 * <li>It constructs the prompts for the AI.</li>
 * <li>It communicates with the AI via the {@link NeuralService} port.</li>
 * <li>It transforms the AI's response into the final
 * {@link AnalysisResult}.</li>
 * </ol>
 * Contrast with {@link ChangeClusterer}, which is purely deterministic and
 * mathematical.
 */
@RequiredArgsConstructor
public class IntentAnalysisService {

    private final ChangeClusteringService changeClusteringService;
    private final NeuralService neuralService;

    /**
     * analyze the intent behind the changes in the library diff.
     *
     * @param diff The library difference model.
     * @return The semantic analysis result.
     */
    public AnalysisResult analyzeIntent(LibraryDiff diff) {
        // 1. Cluster changes to get high-level metrics
        List<PackageChangeSummary> summaries = changeClusteringService.clusterChanges(diff);

        // 2. TODO: Iterate summaries, build prompts, call neuralService
        // For now, returning a dummy result
        return new AnalysisResult("AI Analysis Pending Implementation", Collections.emptyList());
    }
}
