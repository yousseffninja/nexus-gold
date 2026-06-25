package com.rocketeers.nexus_gold.dto.category;

import com.rocketeers.nexus_gold.enums.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CategoryResponse {
    private long id;
    private String name;
    private CategoryType type;
    private String message;
    private boolean success;
}
