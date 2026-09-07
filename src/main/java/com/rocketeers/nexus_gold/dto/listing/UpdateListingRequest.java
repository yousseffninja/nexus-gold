package com.rocketeers.nexus_gold.dto.listing;

import com.rocketeers.nexus_gold.enums.DeliveryMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "Request object for updating an existing listing")
public class UpdateListingRequest {

    @Size(max = 100, message = "Title must not exceed 100 characters")
    @Schema(description = "Listing title", example = "1000 Gold - Fast Delivery")
    private String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    @Schema(description = "Detailed description of the listing", example = "1000 gold coins for your account. Delivered within 1 hour.")
    private String description;

    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    @Schema(description = "Price in USD", example = "10.00")
    private BigDecimal price;

    @Min(value = 1, message = "Quantity must be at least 1")
    @Schema(description = "Available quantity", example = "1")
    private Integer quantity;

    @Schema(description = "Delivery method for the item", example = "FACE_TO_FACE")
    private DeliveryMethod deliveryMethod;

    @Schema(description = "Average delivery time", example = "1-2 hours")
    private String averageDelivery;

    @Schema(description = "List of images for the listing")
    private List<MultipartFile> images;
}