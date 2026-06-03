package com.rocketeers.nexus_gold.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtAuthenticationResponse {
    private boolean success;
    private String accessToken;
    private String refreshToken;
}
