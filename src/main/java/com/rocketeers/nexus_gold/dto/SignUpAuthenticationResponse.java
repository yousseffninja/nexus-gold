package com.rocketeers.nexus_gold.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpAuthenticationResponse {
    private boolean success;
    private String message;
    private Object data;
}
