package com.rocketeers.nexus_gold.dto.sign_in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SignInRequest {

    @Schema(example = "example@mail.com", description = "Email of user should be exist")
    private String email;

    @Schema(example = "Password123", description = "New password. Must be at least 8 characters")
    private String password;

}
