package com.boilerplate.discord.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DiscordMessageRequestDTO {

    /** Plain text message content (max 2000 chars). */
    private String content;

    /** Overrides the webhook's default display name. */
    private String username;

    /** Overrides the webhook's default avatar. */
    private String avatarUrl;

    /** Rich embed objects (max 10). */
    private List<DiscordEmbedDTO> embeds;
}
