package com.argus.utilities.neural.client;

import com.argus.utilities.neural.NeuralConnectionConfig;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OpenAiClient extends BaseNeuralClient {

    public OpenAiClient(NeuralConnectionConfig config) {
        super(config);
    }

    @Override
    public String generate(String prompt) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", config.getModelName() != null ? config.getModelName() : "gpt-4");

            ArrayNode messages = requestBody.putArray("messages");
            ObjectNode message = messages.addObject();
            message.put("role", "user");
            message.put("content", prompt);

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getBaseUrl() != null ? config.getBaseUrl()
                            : "https://api.openai.com/v1/chat/completions"))
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("OpenAI API failed: " + response.body());
            }

            return objectMapper.readTree(response.body())
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to call OpenAI API", e);
        }
    }
}
