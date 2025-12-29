package com.argus.utilities.neural.client;

import com.argus.utilities.neural.NeuralConnectionConfig;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ClaudeClient extends BaseNeuralClient {

    public ClaudeClient(NeuralConnectionConfig config) {
        super(config);
    }

    @Override
    public String generate(String prompt) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", config.getModelName() != null ? config.getModelName() : "claude-3-opus-20240229");
            requestBody.put("max_tokens", 1024);

            ArrayNode messages = requestBody.putArray("messages");
            ObjectNode message = messages.addObject();
            message.put("role", "user");
            message.put("content", prompt);

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getBaseUrl() != null ? config.getBaseUrl()
                            : "https://api.anthropic.com/v1/messages"))
                    .header("x-api-key", config.getApiKey())
                    .header("anthropic-version", "2023-06-01")
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Claude API failed: " + response.body());
            }

            // Simple parsing - in production we'd use a proper response POJO
            return objectMapper.readTree(response.body())
                    .path("content")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to call Claude API", e);
        }
    }
}
