package com.argus.utilities.neural.client;

import com.argus.utilities.neural.NeuralConnectionConfig;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OllamaClient extends BaseNeuralClient {

    public OllamaClient(NeuralConnectionConfig config) {
        super(config);
    }

    @Override
    public String generate(String prompt) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", config.getModelName() != null ? config.getModelName() : "llama2");
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            // Default to localhost for Ollama if not specified
            String baseUrl = config.getBaseUrl() != null ? config.getBaseUrl() : "http://localhost:11434/api/generate";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Ollama API failed: " + response.body());
            }

            return objectMapper.readTree(response.body())
                    .path("response")
                    .asText();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to call Ollama API", e);
        }
    }
}
