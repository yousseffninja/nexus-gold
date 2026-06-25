package com.rocketeers.nexus_gold.dto.sign_up;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SignUpRequest {

    @Schema(example = "john" , description = "first name should be not null")
    private String firstName;
    @Schema(example = "doe" , description = "last name should be not null")
    private String lastName;
    @Schema(example = "example@mail.com" , description = "email should be not null and in form example@mail.com")
    private String email;
    @Schema(example = "password@1234" , description = "password should be not null")
    private String password;
    @Schema(example = "john doe" , description = "displayName  should be not null")
    private String displayName;

}
