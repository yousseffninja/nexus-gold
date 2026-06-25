package com.rocketeers.nexus_gold.dto.category;

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
    private UUID id;
    private String name;
    private String type;
}
