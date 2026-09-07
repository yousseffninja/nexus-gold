package com.rocketeers.nexus_gold.dto.listing;

import com.rocketeers.nexus_gold.enums.DeliveryMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "Request object for creating a new listing")
public class CreateListingRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    @Schema(description = "Listing title", example = "1000 Gold - Fast Delivery", required = true)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Schema(description = "Detailed description of the listing", example = "1000 gold coins for your account. Delivered within 1 hour.", required = true)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Schema(description = "Price in USD", example = "10.00", required = true)
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Schema(description = "Available quantity", example = "1", required = true)
    private Integer quantity;

    @NotNull(message = "Game ID is required")
    @Schema(description = "ID of the game", example = "1", required = true)
    private Long gameId;

    @NotNull(message = "Category ID is required")
    @Schema(description = "ID of the category", example = "1", required = true)
    private Long categoryId;

    @NotNull(message = "Delivery method is required")
    @Schema(description = "Delivery method for the item", example = "FACE_TO_FACE", required = true)
    private DeliveryMethod deliveryMethod;

    @Schema(description = "Average delivery time", example = "1-2 hours")
    private String averageDelivery;

    @Schema(description = "List of images for the listing")
    private List<MultipartFile> images;
}