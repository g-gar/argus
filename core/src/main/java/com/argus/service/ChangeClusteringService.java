package com.argus.service;

import com.argus.model.ai.PackageChangeSummary;
import com.argus.model.diff.LibraryDiff;
import java.util.Collections;
import java.util.List;

/**
 * Service responsible for processing LibraryDiff into metrics and payloads for
 * AI analysis.
 * <p>
 * This service is <b>deterministic</b>. It groups changes, counts breaking
 * changes,
 * and extracts representative signatures. It does <b>not</b> perform ANY
 * semantic inference or AI calls. That responsability lies with
 * {@link IntentAnalysisService}.
 */
public class ChangeClusteringService {

    /**
     * Clusters changes by package and generates summaries.
     * 
     * @param diff The library diff to process.
     * @return A list of package change summaries.
     */
    public List<PackageChangeSummary> clusterChanges(LibraryDiff diff) {
        return Collections.emptyList();
    }
}
