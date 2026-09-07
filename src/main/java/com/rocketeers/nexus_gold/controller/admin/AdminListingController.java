package com.rocketeers.nexus_gold.controller.admin;

import com.rocketeers.nexus_gold.dto.listing.ListingCardResponse;
import com.rocketeers.nexus_gold.enums.ListingStatus;
import com.rocketeers.nexus_gold.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/listings")
@RequiredArgsConstructor
@Tag(name = "Admin - Listings", description = "Admin APIs for managing all listings")
public class AdminListingController {

    private final ListingService listingService;

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Change listing status",
            description = "Force change any listing status (ADMIN only)"
    )
    public ResponseEntity<Void> changeStatus(
            @PathVariable long id,
            @RequestParam ListingStatus status) {
        listingService.adminChangeStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Force delete listing",
            description = "Force delete any listing (ADMIN only)"
    )
    public ResponseEntity<Void> forceDelete(@PathVariable long id) {
        listingService.adminChangeStatus(id, ListingStatus.DELETED);
        return ResponseEntity.noContent().build();
    }
}