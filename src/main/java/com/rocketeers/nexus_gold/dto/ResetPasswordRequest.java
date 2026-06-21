package com.rocketeers.nexus_gold.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @Schema(example = "abc123xyz", description ="Password reset token to identify the user")
    private String passwordResetToken;

    @Schema(example = "12345", description = "code must be active ")
    private String code;

    @Schema(example = "NewPassword123", description = "New password. Must be at least 8 characters")
    private String newPassword;
}
