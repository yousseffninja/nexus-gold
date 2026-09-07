package com.rocketeers.nexus_gold.dto.listing;

import com.rocketeers.nexus_gold.dto.profile.UserProfileResponse;
import com.rocketeers.nexus_gold.enums.DeliveryMethod;
import com.rocketeers.nexus_gold.enums.ListingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Schema(description = "Response object containing full listing details")
public class ListingResponse {
    @Schema(description = "Listing ID", example = "1")
    private long id;
    @Schema(description = "Listing title", example = "1000 Gold - Fast Delivery")
    private String title;
    @Schema(description = "Detailed description of the listing")
    private String description;
    @Schema(description = "Price in USD", example = "10.00")
    private BigDecimal price;
    @Schema(description = "Currency code", example = "USD")
    private String currency;
    @Schema(description = "Available quantity", example = "1")
    private int quantity;
    @Schema(description = "Name of the game", example = "World of Warcraft")
    private String gameName;
    @Schema(description = "URL-friendly game identifier", example = "world-of-warcraft")
    private String gameSlug;
    @Schema(description = "Name of the category", example = "Gold")
    private String categoryName;
    @Schema(description = "Type of the category", example = "GOLD")
    private String categoryType;
    @Schema(description = "Current listing status", example = "ACTIVE")
    private ListingStatus status;
    @Schema(description = "Delivery method", example = "FACE_TO_FACE")
    private DeliveryMethod deliveryMethod;
    @Schema(description = "Average delivery time", example = "1-2 hours")
    private String averageDelivery;
    @Schema(description = "Number of views", example = "150")
    private int viewCount;
    @Schema(description = "List of listing images")
    private List<ListingImageResponse> images;
    @Schema(description = "Seller profile information")
    private UserProfileResponse seller;
    @Schema(description = "Listing creation timestamp")
    private LocalDateTime createdAt;
    @Schema(description = "Listing last update timestamp")
    private LocalDateTime updatedAt;
}