package com.rocketeers.nexus_gold.dto.category;

import com.rocketeers.nexus_gold.enums.CategoryType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryRequest {
    @Schema(example = "Game id", description = "Game id of Category")
    @NotNull(message = "Game id is mandatory")
    private long gameId;

    @Schema(example = "Strategy", description = "name of Category")
    @NotBlank(message = "Category name is mandatory")
    private String name;

    @Schema(example = "Fortnite", description = "Name of games")
    @NotNull(message = "Type name is mandatory")
    private CategoryType type;
}
