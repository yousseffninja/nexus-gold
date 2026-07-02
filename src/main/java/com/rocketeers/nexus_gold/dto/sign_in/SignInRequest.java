package com.rocketeers.nexus_gold.dto.sign_in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignInRequest {

    @Schema(example = "example@mail.com", description = "Email of user should be exist")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @Schema(example = "Password123", description = "New password. Must be at least 8 characters")
    @NotBlank(message = "Password is mandatory")
    private String password;

}
