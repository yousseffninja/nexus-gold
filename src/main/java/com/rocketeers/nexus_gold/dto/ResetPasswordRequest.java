package com.rocketeers.nexus_gold.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @Schema(example = "user@example.com", description ="User email to identify the user")
    private String email;

    @Schema(example = "NewPassword123", description = "New password. Must be at least 8 characters")
    private String newPassword;
}
