package com.argus.utilities.neural;

import com.argus.port.NeuralService;
import com.argus.utilities.neural.client.ClaudeClient;
import com.argus.utilities.neural.client.GeminiClient;
import com.argus.utilities.neural.client.OllamaClient;
import com.argus.utilities.neural.client.OpenAiClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NeuralClientFactoryTest {

    @Test
    void shouldCreateClaudeClient() {
        NeuralConnectionConfig config = NeuralConnectionConfig.builder().apiKey("dummy").build();
        NeuralService client = NeuralClientFactory.createClient(NeuralProvider.CLAUDE, config);
        assertNotNull(client);
        assertInstanceOf(ClaudeClient.class, client);
    }

    @Test
    void shouldCreateGeminiClient() {
        NeuralConnectionConfig config = NeuralConnectionConfig.builder().apiKey("dummy").build();
        NeuralService client = NeuralClientFactory.createClient(NeuralProvider.GEMINI, config);
        assertNotNull(client);
        assertInstanceOf(GeminiClient.class, client);
    }

    @Test
    void shouldCreateOpenAiClient() {
        NeuralConnectionConfig config = NeuralConnectionConfig.builder().apiKey("dummy").build();
        NeuralService client = NeuralClientFactory.createClient(NeuralProvider.OPENAI, config);
        assertNotNull(client);
        assertInstanceOf(OpenAiClient.class, client);
    }

    @Test
    void shouldCreateOllamaClient() {
        NeuralConnectionConfig config = NeuralConnectionConfig.builder().build();
        NeuralService client = NeuralClientFactory.createClient(NeuralProvider.OLLAMA, config);
        assertNotNull(client);
        assertInstanceOf(OllamaClient.class, client);
    }
}
