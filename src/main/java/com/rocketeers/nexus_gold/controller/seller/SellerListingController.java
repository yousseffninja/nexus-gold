package com.rocketeers.nexus_gold.controller.seller;

import com.rocketeers.nexus_gold.dto.listing.CreateListingRequest;
import com.rocketeers.nexus_gold.dto.listing.ListingCardResponse;
import com.rocketeers.nexus_gold.dto.listing.ListingResponse;
import com.rocketeers.nexus_gold.dto.listing.UpdateListingRequest;
import com.rocketeers.nexus_gold.model.User;
import com.rocketeers.nexus_gold.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/seller/listings")
@RequiredArgsConstructor
@Tag(name = "Seller - Listings", description = "Seller APIs for managing listings")
public class SellerListingController {

    private final ListingService listingService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Create listing",
            description = "Create a new listing (SELLER only)"
    )
    public ResponseEntity<ListingResponse> createListing(
            @AuthenticationPrincipal User seller,
            @Valid @ModelAttribute CreateListingRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(listingService.createListing(seller, request));
    }

    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(
            summary = "Update listing",
            description = "Update an existing listing (owner only)"
    )
    public ResponseEntity<ListingResponse> updateListing(
            @AuthenticationPrincipal User seller,
            @PathVariable long id,
            @Valid @ModelAttribute UpdateListingRequest request) {
        return ResponseEntity.ok(
                listingService.updateListing(seller, id, request)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete listing",
            description = "Soft delete a listing (owner only)"
    )
    public ResponseEntity<Void> deleteListing(
            @AuthenticationPrincipal User seller,
            @PathVariable long id) {
        listingService.deleteListing(seller, id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/pause")
    @Operation(
            summary = "Pause or unpause listing",
            description = "Toggle listing between ACTIVE and PAUSED"
    )
    public ResponseEntity<ListingResponse> togglePause(
            @AuthenticationPrincipal User seller,
            @PathVariable long id) {
        return ResponseEntity.ok(
                listingService.togglePause(seller, id)
        );
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get my listings",
            description = "Get all listings created by the current seller"
    )
    public ResponseEntity<Page<ListingCardResponse>> getMyListings(
            @AuthenticationPrincipal User seller,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        return ResponseEntity.ok(
                listingService.getMyListings(seller, pageable)
        );
    }
}