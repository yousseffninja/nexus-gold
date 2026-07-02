package com.rocketeers.nexus_gold.dto.authenrication;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetResponse {

    private boolean success;
    private String message;
    private String passwordResetToken;
}
