package com.argus.port;

/**
 * Interface for AI/LLM providers (Port).
 */
public interface NeuralService {
    /**
     * Generates a response for the given prompt.
     *
     * @param prompt The input prompt.
     * @return The generated text response.
     * @throws RuntimeException if the generation fails.
     */
    String generate(String prompt);
}
