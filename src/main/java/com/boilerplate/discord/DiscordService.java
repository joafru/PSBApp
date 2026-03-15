package com.boilerplate.discord;

import com.boilerplate.discord.dto.DiscordEmbedDTO;
import com.boilerplate.discord.dto.DiscordEmbedFieldDTO;
import com.boilerplate.discord.dto.DiscordMessageRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

/**
 * High-level service for sending Discord notifications.
 *
 * <p>Provides convenience methods on top of {@link DiscordClient}:
 * <ul>
 *   <li>{@link #sendSimpleMessage(String)} — plain text</li>
 *   <li>{@link #sendEmbed(DiscordEmbedDTO)} — single rich embed</li>
 *   <li>{@link #sendEmbeds(List)} — multiple embeds</li>
 *   <li>{@link #sendMessage(DiscordMessageRequestDTO)} — full control</li>
 * </ul>
 *
 * <p>Usage example:
 * <pre>
 * discordService.sendSimpleMessage("Server started!");
 *
 * discordService.sendEmbed(DiscordEmbedDTO.builder()
 *     .title("New user registered")
 *     .description("john@example.com just signed up")
 *     .color(5814783)
 *     .build());
 * </pre>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiscordService {

    private final DiscordClient discordClient;

    @Value("${app.discord.username:Boilerplate Bot}")
    private String defaultUsername;

    // ── Public API ───────────────────────────────────────────────────────────

    /**
     * Sends a plain text message.
     */
    public void sendSimpleMessage(String content) {
        send(DiscordMessageRequestDTO.builder()
                .content(content)
                .username(defaultUsername)
                .build());
    }

    /**
     * Sends a single rich embed.
     */
    public void sendEmbed(DiscordEmbedDTO embed) {
        sendEmbeds(List.of(embed));
    }

    /**
     * Sends up to 10 rich embeds in a single message.
     */
    public void sendEmbeds(List<DiscordEmbedDTO> embeds) {
        send(DiscordMessageRequestDTO.builder()
                .username(defaultUsername)
                .embeds(embeds)
                .build());
    }

    /**
     * Sends a fully customised message payload.
     */
    public void sendMessage(DiscordMessageRequestDTO message) {
        send(message);
    }

    /**
     * Convenience method: sends a titled embed with fields.
     *
     * <pre>
     * discordService.sendInfoEmbed("Deployment", 5814783,
     *     DiscordEmbedFieldDTO.builder().name("Version").value("1.2.0").build(),
     *     DiscordEmbedFieldDTO.builder().name("Environment").value("Production").build()
     * );
     * </pre>
     */
    public void sendInfoEmbed(String title, int color, DiscordEmbedFieldDTO... fields) {
        sendEmbed(DiscordEmbedDTO.builder()
                .title(title)
                .color(color)
                .timestamp(Instant.now().toString())
                .fields(List.of(fields))
                .build());
    }

    // ── Private ──────────────────────────────────────────────────────────────

    private void send(DiscordMessageRequestDTO payload) {
        try {
            discordClient.sendMessage(payload);
            log.debug("Discord message sent successfully");
        } catch (Exception e) {
            log.error("Failed to send Discord message: {}", e.getMessage(), e);
        }
    }
}
