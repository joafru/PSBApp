package com.boilerplate.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Reusable base for any REST client backed by Spring {@link WebClient}.
 *
 * <h2>How to extend</h2>
 * <pre>
 * {@literal @}Component
 * public class PaymentApiClient extends BaseWebClientService {
 *
 *     public PaymentApiClient(WebClient.Builder builder,
 *                             {@literal @}Value("${payment.api.url}") String baseUrl) {
 *         super(builder, baseUrl);
 *     }
 *
 *     public PaymentResponse charge(ChargeRequest req) {
 *         return post("/charge", req, PaymentResponse.class);
 *     }
 * }
 * </pre>
 *
 * <p>Extra headers can be passed per-call (e.g. API keys):
 * <pre>
 *     get("/resource", Response.class, Map.of("X-Api-Key", key));
 * </pre>
 */
@Slf4j
public abstract class BaseWebClientService {

    protected final WebClient webClient;

    protected BaseWebClientService(WebClient.Builder builder, String baseUrl) {
        this.webClient = builder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    // ── GET ──────────────────────────────────────────────────────────────────

    protected <T> T get(String uri, Class<T> responseType) {
        return get(uri, responseType, null);
    }

    protected <T> T get(String uri, Class<T> responseType, Map<String, String> headers) {
        return webClient.get()
                .uri(uri)
                .headers(applyHeaders(headers))
                .retrieve()
                .bodyToMono(responseType)
                .doOnError(WebClientResponseException.class,
                        e -> log.error("GET {} → {} {}", uri, e.getStatusCode(), e.getResponseBodyAsString()))
                .onErrorMap(this::wrap)
                .block();
    }

    // ── POST ─────────────────────────────────────────────────────────────────

    protected <B, T> T post(String uri, B body, Class<T> responseType) {
        return post(uri, body, responseType, null);
    }

    protected <B, T> T post(String uri, B body, Class<T> responseType, Map<String, String> headers) {
        return webClient.post()
                .uri(uri)
                .headers(applyHeaders(headers))
                .body(Mono.just(body), body.getClass())
                .retrieve()
                .bodyToMono(responseType)
                .doOnError(WebClientResponseException.class,
                        e -> log.error("POST {} → {} {}", uri, e.getStatusCode(), e.getResponseBodyAsString()))
                .onErrorMap(this::wrap)
                .block();
    }

    // ── PUT ──────────────────────────────────────────────────────────────────

    protected <B, T> T put(String uri, B body, Class<T> responseType) {
        return put(uri, body, responseType, null);
    }

    protected <B, T> T put(String uri, B body, Class<T> responseType, Map<String, String> headers) {
        return webClient.put()
                .uri(uri)
                .headers(applyHeaders(headers))
                .body(Mono.just(body), body.getClass())
                .retrieve()
                .bodyToMono(responseType)
                .doOnError(WebClientResponseException.class,
                        e -> log.error("PUT {} → {} {}", uri, e.getStatusCode(), e.getResponseBodyAsString()))
                .onErrorMap(this::wrap)
                .block();
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    protected void delete(String uri) {
        delete(uri, null);
    }

    protected void delete(String uri, Map<String, String> headers) {
        webClient.delete()
                .uri(uri)
                .headers(applyHeaders(headers))
                .retrieve()
                .bodyToMono(Void.class)
                .doOnError(WebClientResponseException.class,
                        e -> log.error("DELETE {} → {} {}", uri, e.getStatusCode(), e.getResponseBodyAsString()))
                .onErrorMap(this::wrap)
                .block();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private Consumer<HttpHeaders> applyHeaders(Map<String, String> extra) {
        return h -> {
            if (extra != null) extra.forEach(h::add);
        };
    }

    private Throwable wrap(Throwable t) {
        if (t instanceof WebClientResponseException ex) {
            return new RuntimeException(
                    "External API error " + ex.getStatusCode() + ": " + ex.getResponseBodyAsString(), ex);
        }
        return new RuntimeException("External call failed: " + t.getMessage(), t);
    }
}
