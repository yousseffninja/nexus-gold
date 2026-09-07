package com.rocketeers.nexus_gold.dto.listing;

import com.rocketeers.nexus_gold.enums.DeliveryMethod;
import com.rocketeers.nexus_gold.enums.ListingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Response object for listing card (summary view in search results)")
public class ListingCardResponse {
    @Schema(description = "Listing ID", example = "1")
    private long id;
    @Schema(description = "Listing title", example = "1000 Gold - Fast Delivery")
    private String title;
    @Schema(description = "Price in USD", example = "10.00")
    private BigDecimal price;
    @Schema(description = "Currency code", example = "USD")
    private String currency;
    @Schema(description = "Name of the game", example = "World of Warcraft")
    private String gameName;
    @Schema(description = "URL-friendly game identifier", example = "world-of-warcraft")
    private String gameSlug;
    @Schema(description = "Name of the category", example = "Gold")
    private String categoryName;
    @Schema(description = "Type of the category", example = "GOLD")
    private String categoryType;
    @Schema(description = "Seller's display name", example = "ProSeller123")
    private String sellerDisplayName;
    @Schema(description = "Seller's rating", example = "4.8")
    private double sellerRating;
    @Schema(description = "URL of the listing thumbnail image")
    private String thumbnailUrl;
    @Schema(description = "Current listing status", example = "ACTIVE")
    private ListingStatus status;
    @Schema(description = "Delivery method", example = "FACE_TO_FACE")
    private DeliveryMethod deliveryMethod;
    @Schema(description = "Average delivery time", example = "1-2 hours")
    private String averageDelivery;
    @Schema(description = "Listing creation timestamp")
    private LocalDateTime createdAt;
}