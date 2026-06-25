package com.rocketeers.nexus_gold.dto.verify_rest_code;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VerifyResetCodeRequest {

    @Schema(example = "example@mail.com", description = "Email of the user")
    private String email;

    @Schema(example = "12345", description = "Reset code sent to email")
    private String code;
}
