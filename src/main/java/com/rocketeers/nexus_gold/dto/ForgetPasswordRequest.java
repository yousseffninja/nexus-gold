package com.rocketeers.nexus_gold.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ForgetPasswordRequest {
    @Schema(example = "example@mail.com", description = "Email needed to reset password")
    private String email;
}
