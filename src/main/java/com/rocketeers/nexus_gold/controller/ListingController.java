package com.rocketeers.nexus_gold.controller;

import com.rocketeers.nexus_gold.dto.listing.ListingCardResponse;
import com.rocketeers.nexus_gold.dto.listing.ListingResponse;
import com.rocketeers.nexus_gold.enums.DeliveryMethod;
import com.rocketeers.nexus_gold.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
@SecurityRequirements
@Tag(name = "Listings", description = "Public APIs for browsing listings")
public class ListingController {

    private final ListingService listingService;

    @GetMapping
    @Operation(
            summary = "Search listings",
            description = "Search and filter all active listings"
    )
    public ResponseEntity<Page<ListingCardResponse>> searchListings(
            @RequestParam(required = false) String gameSlug,
            @RequestParam(required = false) String categoryType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) DeliveryMethod deliveryMethod,
            @RequestParam(required = false) Double minSellerRating,
            @RequestParam(required = false) String search,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        return ResponseEntity.ok(
                listingService.searchListings(
                        gameSlug, categoryType, minPrice, maxPrice,
                        deliveryMethod, minSellerRating, search, pageable
                )
        );
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get listing by ID",
            description = "Get full listing details by ID"
    )
    public ResponseEntity<ListingResponse> getListingById(
            @PathVariable long id) {
        return ResponseEntity.ok(listingService.getListingById(id));
    }

    @GetMapping("/game/{slug}")
    @Operation(
            summary = "Get listings by game",
            description = "Get all active listings for a specific game"
    )
    public ResponseEntity<Page<ListingCardResponse>> getListingsByGame(
            @PathVariable String slug,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        return ResponseEntity.ok(
                listingService.getListingsByGame(slug, pageable)
        );
    }

    @GetMapping("/seller/{displayName}")
    @Operation(
            summary = "Get listings by seller",
            description = "Get all active listings for a specific seller"
    )
    public ResponseEntity<Page<ListingCardResponse>> getListingsBySeller(
            @PathVariable String displayName,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        return ResponseEntity.ok(
                listingService.getListingsBySeller(displayName, pageable)
        );
    }
}