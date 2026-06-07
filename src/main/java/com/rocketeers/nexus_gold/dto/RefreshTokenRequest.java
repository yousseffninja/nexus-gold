package com.rocketeers.nexus_gold.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @Schema(example = "token", description = "Current token for the authenticated user")
    private String token;

}
