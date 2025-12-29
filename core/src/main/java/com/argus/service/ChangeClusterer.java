package com.argus.service;

import com.argus.model.ai.PackageChangeSummary;
import com.argus.model.diff.LibraryDiff;
import java.util.Collections;
import java.util.List;

/**
 * Service responsible for processing LibraryDiff into metrics and payloads for
 * AI analysis.
 */
public class ChangeClusterer {

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
