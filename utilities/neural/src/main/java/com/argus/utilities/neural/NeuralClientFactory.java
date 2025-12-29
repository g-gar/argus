package com.argus.utilities.neural;

import com.argus.port.NeuralService;
import com.argus.utilities.neural.client.ClaudeClient;
import com.argus.utilities.neural.client.GeminiClient;
import com.argus.utilities.neural.client.OllamaClient;
import com.argus.utilities.neural.client.OpenAiClient;

/**
 * Factory for creating NeuralClient instances.
 */
public class NeuralClientFactory {

    private NeuralClientFactory() {
    }

    /**
     * Creates a NeuralClient for the given provider and configuration.
     *
     * @param provider The AI provider.
     * @param config   The connection configuration.
     * @return A configured NeuralClient.
     * @throws IllegalArgumentException if the provider is not supported.
     */
    public static NeuralService createClient(NeuralProvider provider, NeuralConnectionConfig config) {
        switch (provider) {
            case CLAUDE:
                return new ClaudeClient(config);
            case GEMINI:
                return new GeminiClient(config);
            case OPENAI:
                return new OpenAiClient(config);
            case OLLAMA:
                return new OllamaClient(config);
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }
}
