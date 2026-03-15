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
public class DiscordEmbedDTO {

    private String title;
    private String description;
    /** Decimal color value (e.g. 5814783 = #58B9FF). */
    private Integer color;
    private String url;
    private String timestamp;
    private List<DiscordEmbedFieldDTO> fields;
    private FooterDTO footer;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FooterDTO {
        private String text;
        private String iconUrl;
    }
}
