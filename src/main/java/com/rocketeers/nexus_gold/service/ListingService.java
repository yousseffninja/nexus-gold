package com.rocketeers.nexus_gold.service;


import com.rocketeers.nexus_gold.dto.listing.CreateListingRequest;
import com.rocketeers.nexus_gold.dto.listing.ListingCardResponse;
import com.rocketeers.nexus_gold.dto.listing.ListingResponse;
import com.rocketeers.nexus_gold.dto.listing.UpdateListingRequest;
import com.rocketeers.nexus_gold.enums.DeliveryMethod;
import com.rocketeers.nexus_gold.enums.ListingStatus;
import com.rocketeers.nexus_gold.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

public interface ListingService {


    // ─── Create ───────────────────────────────────────────────────────────
    @Transactional
    ListingResponse createListing(
            User seller,
            CreateListingRequest request);

    // ─── Update ───────────────────────────────────────────────────────────
    @Transactional
    ListingResponse updateListing(
            User seller,
            long listingId,
            UpdateListingRequest request);

    // ─── Delete (soft) ────────────────────────────────────────────────────
    @Transactional
    void deleteListing(User seller, long listingId);

    // ─── Pause / Unpause ──────────────────────────────────────────────────
    @Transactional
    ListingResponse togglePause(User seller, long listingId);

    // ─── Get single listing ───────────────────────────────────────────────
    @Transactional
    ListingResponse getListingById(long listingId);

    // ─── Get listings by game ─────────────────────────────────────────────
    Page<ListingCardResponse> getListingsByGame(
            String gameSlug,
            Pageable pageable);

    // ─── Search with filters ──────────────────────────────────────────────
    Page<ListingCardResponse> searchListings(
            String gameSlug,
            String categoryType,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            DeliveryMethod deliveryMethod,
            Double minSellerRating,
            String search,
            Pageable pageable);

    // ─── Get seller public listings ───────────────────────────────────────
    Page<ListingCardResponse> getListingsBySeller(
            String displayName,
            Pageable pageable);

    // ─── Get my listings ──────────────────────────────────────────────────
    Page<ListingCardResponse> getMyListings(
            User seller,
            Pageable pageable);

    // ─── Admin force status change ────────────────────────────────────────
    @Transactional
    void adminChangeStatus(long listingId, ListingStatus status);

    // ─── Mark as sold (called from OrderService later) ────────────────────
    @Transactional
    void markAsSoldIfEmpty(long listingId);

    // ─── Async view count increment ───────────────────────────────────────
    @Async
    void incrementViewCountAsync(long listingId);
}
