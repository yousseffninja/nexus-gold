package com.rocketeers.nexus_gold.dto.authenrication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ForgetPasswordRequest {
    @Schema(example = "example@mail.com", description = "Email needed to reset password")
    @NotBlank(message = "Email is mandatory")
    private String email;
}
