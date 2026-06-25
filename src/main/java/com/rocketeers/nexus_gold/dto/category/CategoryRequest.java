package com.rocketeers.nexus_gold.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {
    @Schema(example = "Strategy", description = "name of Category")
    @NotBlank(message = "Category name is mandatory")
    private String name;

    @Schema(example = "Fortnite", description = "Name of games")
    @NotBlank(message = "Type name is mandatory")
    private String type;
}
