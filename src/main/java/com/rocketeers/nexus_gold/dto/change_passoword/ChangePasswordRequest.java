package com.rocketeers.nexus_gold.dto.change_passoword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @Schema(example = "OldPassword123", description = "Current password for the authenticated user")
    @NotBlank(message = "Current passord is mandatory")
    private String currentPassword;

    @Schema(example = "NewPassword123", description = "New password. Must be at least 8 characters")
    @NotBlank(message = "New password is mandatory")
    private String newPassword;
}
