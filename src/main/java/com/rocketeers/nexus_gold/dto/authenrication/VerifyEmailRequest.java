package com.rocketeers.nexus_gold.dto.authenrication;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class VerifyEmailRequest {

    @Schema(example = "example@mail.com" , description = "email should be not null and in form example@mail.com")
    private String email;

    @Schema(example = "12345" , description = "code must be active")
    private String code;
}
