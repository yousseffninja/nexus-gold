package com.rocketeers.nexus_gold.dto.email_verfication;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EmailVerificationCodeRequest {
    @Schema(example = "example@mail.com", description = "Email of user should be exist")
    @NotBlank(message = "Email is mandatory")
    private String email;
}