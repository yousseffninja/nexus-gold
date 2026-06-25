package com.rocketeers.nexus_gold.dto.game;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GameRespose {
    private UUID id;
    private String name;
    private String slug;
    private String iconUrl;

}
