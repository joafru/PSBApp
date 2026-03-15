package com.boilerplate.discord;

import com.boilerplate.client.BaseWebClientService;
import com.boilerplate.discord.dto.DiscordMessageRequestDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Low-level Discord webhook client.
 *
 * <p>Uses the incoming webhook URL as the base, so every call is a POST to "/"
 * (i.e., the webhook URL itself). Configure the URL in {@code application.yml}:
 *
 * <pre>
 * app:
 *   discord:
 *     webhook-url: https://discord.com/api/webhooks/{id}/{token}
 * </pre>
 */
@Component
public class DiscordClient extends BaseWebClientService {

    public DiscordClient(
            WebClient.Builder webClientBuilder,
            @Value("${app.discord.webhook-url}") String webhookUrl) {
        super(webClientBuilder, webhookUrl);
    }

    /**
     * Sends a message payload to the configured Discord webhook.
     * Discord returns 204 No Content on success, so we use Void.
     */
    public void sendMessage(DiscordMessageRequestDTO payload) {
        post("", payload, Void.class);
    }
}
