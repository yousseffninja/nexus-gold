package com.rocketeers.nexus_gold.dto.authenrication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @Schema(example = "user@example.com", description ="User email to identify the user")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @Schema(example = "NewPassword123", description = "New password. Must be at least 8 characters")
    @NotBlank(message = "New password is mandatory")
    private String newPassword;
}
