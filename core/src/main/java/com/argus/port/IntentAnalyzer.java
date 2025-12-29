package com.argus.port;

import com.argus.model.ai.AnalysisResult;
import com.argus.model.diff.LibraryDiff;

/**
 * Interface for analyzing the intent behind library changes.
 */
public interface IntentAnalyzer {

    /**
     * Orchestrates the analysis of a library diff to determine the intent behind
     * changes.
     *
     * @param diff The library difference model containing all changes.
     * @return An {@link AnalysisResult} containing the overall summary and
     *         package-level intents.
     */
    AnalysisResult analyzeIntent(LibraryDiff diff);

}
