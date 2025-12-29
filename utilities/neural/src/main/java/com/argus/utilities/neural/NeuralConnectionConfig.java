package com.argus.utilities.neural;

import lombok.Builder;
import lombok.Data;

/**
 * Configuration for connecting to an AI provider.
 */
@Data
@Builder
public class NeuralConnectionConfig {
    /**
     * The API key for authentication.
     */
    private String apiKey;

    /**
     * The base URL for the API endpoint (optional, defaults to provider specific
     * default).
     */
    private String baseUrl;

    /**
     * The specific model name to use (e.g., "gpt-4", "claude-3-opus").
     */
    private String modelName;

    /**
     * Timeout in seconds for API requests.
     */
    private int timeoutSeconds;
}
