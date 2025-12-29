package com.argus.utilities.neural.client;

import com.argus.port.NeuralService;
import com.argus.utilities.neural.NeuralConnectionConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Abstract base client for NeuralClient implementations.
 */
public abstract class BaseNeuralClient implements NeuralService {

    protected final NeuralConnectionConfig config;
    protected final HttpClient httpClient;
    protected final ObjectMapper objectMapper;

    protected BaseNeuralClient(NeuralConnectionConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(config.getTimeoutSeconds() > 0 ? config.getTimeoutSeconds() : 30))
                .build();
        this.objectMapper = new ObjectMapper();
    }
}
