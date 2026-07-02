package com.rocketeers.nexus_gold.dto.email_verfication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailVerificationResponse {
    private boolean success;
    private String message;
}