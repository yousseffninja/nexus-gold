package com.rocketeers.nexus_gold.dto.authenrication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshTokenRequest {

    @Schema(example = "token", description = "Current token for the authenticated user")
    @NotBlank(message = "Token is mandatory")
    private String token;

}
