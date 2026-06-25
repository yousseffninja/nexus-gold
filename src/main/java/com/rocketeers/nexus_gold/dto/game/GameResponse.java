package com.rocketeers.nexus_gold.dto.game;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GameResponse {
    private long id;
    private String name;
    private String slug;
    private String iconUrl;
    private String message;
    private boolean success;

}
