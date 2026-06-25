package com.rocketeers.nexus_gold.dto.change_passoword;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @Schema(example = "OldPassword123", description = "Current password for the authenticated user")
    private String currentPassword;

    @Schema(example = "NewPassword123", description = "New password. Must be at least 8 characters")
    private String newPassword;
}
