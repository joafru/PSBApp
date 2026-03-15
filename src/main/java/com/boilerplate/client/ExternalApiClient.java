package com.boilerplate.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Example integration client.
 *
 * <p>Replace the base URL and add domain-specific methods.
 * The boilerplate hit JSONPlaceholder as a safe default.
 *
 * <pre>
 * // application.yml
 * app:
 *   external-api:
 *     base-url: https://your-real-api.com
 * </pre>
 */
@Component
public class ExternalApiClient extends BaseWebClientService {

    public ExternalApiClient(
            WebClient.Builder webClientBuilder,
            @Value("${app.external-api.base-url}") String baseUrl) {
        super(webClientBuilder, baseUrl);
    }

    /**
     * Demo call — GET /todos/{id}.
     * Replace with your actual API methods.
     */
    public String getTodo(Long id) {
        return get("/todos/" + id, String.class);
    }

    /*
     * Template for new integrations:
     *
     * public SomeResponse createResource(SomeRequest request) {
     *     return post("/resource", request, SomeResponse.class);
     * }
     *
     * public SomeResponse updateResource(Long id, SomeRequest request) {
     *     return put("/resource/" + id, request, SomeResponse.class);
     * }
     *
     * public void removeResource(Long id) {
     *     delete("/resource/" + id);
     * }
     */
}
