package com.rocketeers.nexus_gold.dto.listing;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "Response object for listing image details")
public class ListingImageResponse {
    @Schema(description = "Image ID", example = "1")
    private long id;
    @Schema(description = "URL of the image", example = "https://example.com/images/listing1.jpg")
    private String imageUrl;
    @Schema(description = "Display order in the listing", example = "1")
    private int displayOrder;
}