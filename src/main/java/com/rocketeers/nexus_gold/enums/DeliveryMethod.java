package com.rocketeers.nexus_gold.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Delivery method for listing items")
public enum DeliveryMethod {
    @Schema(description = "Instant delivery via game mail or automated system")
    INSTANT,
    @Schema(description = "Manual delivery via face-to-face trade or other manual methods")
    MANUAL
}