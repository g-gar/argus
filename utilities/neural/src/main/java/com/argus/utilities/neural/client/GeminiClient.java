package com.argus.utilities.neural.client;

import com.argus.utilities.neural.NeuralConnectionConfig;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GeminiClient extends BaseNeuralClient {

    public GeminiClient(NeuralConnectionConfig config) {
        super(config);
    }

    @Override
    public String generate(String prompt) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            ArrayNode contents = requestBody.putArray("contents");
            ObjectNode parts = contents.addObject().putArray("parts").addObject();
            parts.put("text", prompt);

            String model = config.getModelName() != null ? config.getModelName() : "gemini-pro";
            String url = (config.getBaseUrl() != null ? config.getBaseUrl()
                    : "https://generativelanguage.googleapis.com/v1beta/models/")
                    + model + ":generateContent?key=" + config.getApiKey();

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Gemini API failed: " + response.body());
            }

            return objectMapper.readTree(response.body())
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to call Gemini API", e);
        }
    }
}
