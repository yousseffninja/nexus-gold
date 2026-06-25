package com.rocketeers.nexus_gold.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GameRequest {
    @Schema(example = "Fortnite", description = "Name of games")
    @NotBlank(message = "Game name is mandatory")
    private String name;

    @Schema(example = "fortnite", description = "Slug of games")
    @NotBlank(message = "Slug is mandatory")
    private String slug;

    @Schema(example = "imgurl.com", description = "Image of category")
    private String iconUrl;
}
