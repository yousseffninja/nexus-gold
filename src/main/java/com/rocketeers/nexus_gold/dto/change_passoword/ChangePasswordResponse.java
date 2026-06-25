package com.rocketeers.nexus_gold.dto.change_passoword;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordResponse {
    private boolean success;
    private String message;
}
