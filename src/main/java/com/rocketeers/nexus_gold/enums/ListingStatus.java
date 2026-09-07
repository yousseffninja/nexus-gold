package com.rocketeers.nexus_gold.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status of a listing")
public enum ListingStatus {
    @Schema(description = "Listing is active and visible to buyers")
    ACTIVE,
    @Schema(description = "Listing has been sold and is no longer available")
    SOLD,
    @Schema(description = "Listing is temporarily paused by the seller")
    PAUSED,
    @Schema(description = "Listing has been deleted (soft delete)")
    DELETED
}