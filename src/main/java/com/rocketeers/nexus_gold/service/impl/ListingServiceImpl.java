package com.rocketeers.nexus_gold.service.impl;

import com.rocketeers.nexus_gold.dto.listing.*;
import com.rocketeers.nexus_gold.dto.profile.UserProfileResponse;
import com.rocketeers.nexus_gold.enums.DeliveryMethod;
import com.rocketeers.nexus_gold.enums.ListingStatus;
import com.rocketeers.nexus_gold.exception.ResourceNotFoundException;
import com.rocketeers.nexus_gold.model.*;
import com.rocketeers.nexus_gold.repository.*;
import com.rocketeers.nexus_gold.repository.specification.ListingSpecification;
import com.rocketeers.nexus_gold.service.CloudinaryService;
import com.rocketeers.nexus_gold.service.ListingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListingServiceImpl implements ListingService {

    private final ListingRepository listingRepository;
    private final ListingImageRepository listingImageRepository;
    private final GameRepository gameRepository;
    private final CategoryRepository categoryRepository;
    private final UserProfileRepository userProfileRepository;
    private final CloudinaryService cloudinaryService;

    // ─── Create ───────────────────────────────────────────────────────────
    @Transactional
    @Override
    public ListingResponse createListing(
            User seller,
            CreateListingRequest request) {

        // Validate images
        if (request.getImages() == null || request.getImages().isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one image is required"
            );
        }
        if (request.getImages().size() > 5) {
            throw new IllegalArgumentException(
                    "Maximum 5 images allowed per listing"
            );
        }

        // Fetch game
        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Game not found"
                ));

        // Fetch category
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found"
                ));

        // Build listing
        Listing listing = Listing.builder()
                .seller(seller)
                .game(game)
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .deliveryMethod(request.getDeliveryMethod())
                .averageDelivery(request.getAverageDelivery())
                .status(ListingStatus.ACTIVE)
                .build();

        Listing saved = listingRepository.save(listing);

        // Upload images to Cloudinary
        List<ListingImage> images = new ArrayList<>();
        List<MultipartFile> files = request.getImages();
        for (int i = 0; i < files.size(); i++) {
            String imageUrl = cloudinaryService.uploadImage(
                    files.get(i),
                    "nexusgold/listings"
            );
            images.add(ListingImage.builder()
                    .listing(saved)
                    .imageUrl(imageUrl)
                    .displayOrder(i)
                    .build());
        }
        listingImageRepository.saveAll(images);
        saved.setImages(images);

        return toFullResponse(saved);
    }

    // ─── Update ───────────────────────────────────────────────────────────
    @Transactional
    @Override
    public ListingResponse updateListing(
            User seller,
            long listingId,
            UpdateListingRequest request) {

        Listing listing = getListingOrThrow(listingId);
        assertOwner(seller, listing);

        if (listing.getStatus() == ListingStatus.SOLD) {
            throw new IllegalStateException(
                    "Cannot edit a sold listing"
            );
        }

        if (request.getTitle() != null) {
            listing.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            listing.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            listing.setPrice(request.getPrice());
        }
        if (request.getQuantity() != null) {
            listing.setQuantity(request.getQuantity());
        }
        if (request.getDeliveryMethod() != null) {
            listing.setDeliveryMethod(request.getDeliveryMethod());
        }
        if (request.getAverageDelivery() != null) {
            listing.setAverageDelivery(request.getAverageDelivery());
        }

        // Replace images if new ones are provided
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            if (request.getImages().size() > 5) {
                throw new IllegalArgumentException(
                        "Maximum 5 images allowed"
                );
            }
            listingImageRepository.deleteAllByListingId(listingId);

            List<ListingImage> newImages = new ArrayList<>();
            List<MultipartFile> files = request.getImages();
            for (int i = 0; i < files.size(); i++) {
                String imageUrl = cloudinaryService.uploadImage(
                        files.get(i),
                        "nexusgold/listings"
                );
                newImages.add(ListingImage.builder()
                        .listing(listing)
                        .imageUrl(imageUrl)
                        .displayOrder(i)
                        .build());
            }
            listingImageRepository.saveAll(newImages);
            listing.setImages(newImages);
        }

        return toFullResponse(listingRepository.save(listing));
    }

    // ─── Delete (soft) ────────────────────────────────────────────────────
    @Transactional
    @Override
    public void deleteListing(User seller, long listingId) {
        Listing listing = getListingOrThrow(listingId);
        assertOwner(seller, listing);
        listing.setStatus(ListingStatus.DELETED);
        listingRepository.save(listing);
    }

    // ─── Pause / Unpause ──────────────────────────────────────────────────
    @Transactional
    @Override
    public ListingResponse togglePause(User seller, long listingId) {
        Listing listing = getListingOrThrow(listingId);
        assertOwner(seller, listing);

        if (listing.getStatus() == ListingStatus.ACTIVE) {
            listing.setStatus(ListingStatus.PAUSED);
        } else if (listing.getStatus() == ListingStatus.PAUSED) {
            listing.setStatus(ListingStatus.ACTIVE);
        } else {
            throw new IllegalStateException(
                    "Cannot pause/unpause a listing with status: "
                            + listing.getStatus()
            );
        }

        return toFullResponse(listingRepository.save(listing));
    }

    // ─── Get single listing ───────────────────────────────────────────────
    @Transactional
    @Override
    public ListingResponse getListingById(long listingId) {
        Listing listing = getListingOrThrow(listingId);
        incrementViewCountAsync(listingId);
        return toFullResponse(listing);
    }

    // ─── Get listings by game ─────────────────────────────────────────────
    @Override
    public Page<ListingCardResponse> getListingsByGame(
            String gameSlug,
            Pageable pageable) {
        return listingRepository
                .findAllByStatusAndGameSlug(
                        ListingStatus.ACTIVE, gameSlug, pageable
                )
                .map(this::toCardResponse);
    }

    // ─── Search with filters ──────────────────────────────────────────────
    @Override
    public Page<ListingCardResponse> searchListings(
            String gameSlug,
            String categoryType,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            DeliveryMethod deliveryMethod,
            Double minSellerRating,
            String search,
            Pageable pageable) {

        Specification<Listing> spec = ListingSpecification.filter(
                gameSlug, categoryType, minPrice, maxPrice,
                deliveryMethod, minSellerRating, search
        );

        return listingRepository.findAll(spec, pageable)
                .map(this::toCardResponse);
    }

    // ─── Get seller public listings ───────────────────────────────────────
    @Override
    public Page<ListingCardResponse> getListingsBySeller(
            String displayName,
            Pageable pageable) {
        return listingRepository
                .findAllByStatusAndSellerDisplayName(
                        ListingStatus.ACTIVE, displayName, pageable
                )
                .map(this::toCardResponse);
    }

    // ─── Get my listings ──────────────────────────────────────────────────
    @Override
    public Page<ListingCardResponse> getMyListings(
            User seller,
            Pageable pageable) {
        return listingRepository
                .findAllBySellerId(seller.getId(), pageable)
                .map(this::toCardResponse);
    }

    // ─── Admin force status change ────────────────────────────────────────
    @Transactional
    @Override
    public void adminChangeStatus(long listingId, ListingStatus status) {
        Listing listing = getListingOrThrow(listingId);
        listing.setStatus(status);
        listingRepository.save(listing);
    }

    // ─── Mark as sold (called from OrderService later) ────────────────────
    @Transactional
    @Override
    public void markAsSoldIfEmpty(long listingId) {
        Listing listing = getListingOrThrow(listingId);
        if (listing.getQuantity() <= 0) {
            listing.setStatus(ListingStatus.SOLD);
            listingRepository.save(listing);
        }
    }

    // ─── Async view count increment ───────────────────────────────────────
    @Async
    @Override
    public void incrementViewCountAsync(long listingId) {
        listingRepository.incrementViewCount(listingId);
    }

    // ─── Private helpers ──────────────────────────────────────────────────
    private Listing getListingOrThrow(long id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Listing not found"
                ));
    }

    private void assertOwner(User seller, Listing listing) {
        if (listing.getSeller().getId() != seller.getId()) {
            throw new AccessDeniedException(
                    "You do not have permission to modify this listing"
            );
        }
    }

    private String getThumbnail(Listing listing) {
        if (listing.getImages() == null || listing.getImages().isEmpty()) {
            return null;
        }
        return listing.getImages().get(0).getImageUrl();
    }

    private ListingCardResponse toCardResponse(Listing listing) {
        UserProfile sellerProfile = userProfileRepository
                .findByUserId(listing.getSeller().getId())
                .orElse(null);

        return ListingCardResponse.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .price(listing.getPrice())
                .currency(listing.getCurrency())
                .gameName(listing.getGame().getName())
                .gameSlug(listing.getGame().getSlug())
                .categoryName(listing.getCategory().getName())
                .categoryType(listing.getCategory().getType().name())
                .sellerDisplayName(listing.getSeller().getDisplayName())
                .sellerRating(sellerProfile != null
                        ? sellerProfile.getRating() : 0.0)
                .thumbnailUrl(getThumbnail(listing))
                .status(listing.getStatus())
                .deliveryMethod(listing.getDeliveryMethod())
                .averageDelivery(listing.getAverageDelivery())
                .createdAt(listing.getCreatedAt())
                .build();
    }

    private ListingResponse toFullResponse(Listing listing) {
        UserProfile sellerProfile = userProfileRepository
                .findByUserId(listing.getSeller().getId())
                .orElse(null);

        List<ListingImageResponse> imageResponses = listing.getImages()
                .stream()
                .map(img -> ListingImageResponse.builder()
                        .id(img.getId())
                        .imageUrl(img.getImageUrl())
                        .displayOrder(img.getDisplayOrder())
                        .build())
                .toList();

        return ListingResponse.builder()
                .id(listing.getId())
                .title(listing.getTitle())
                .description(listing.getDescription())
                .price(listing.getPrice())
                .currency(listing.getCurrency())
                .quantity(listing.getQuantity())
                .gameName(listing.getGame().getName())
                .gameSlug(listing.getGame().getSlug())
                .categoryName(listing.getCategory().getName())
                .categoryType(listing.getCategory().getType().name())
                .status(listing.getStatus())
                .deliveryMethod(listing.getDeliveryMethod())
                .averageDelivery(listing.getAverageDelivery())
                .viewCount(listing.getViewCount())
                .images(imageResponses)
                .seller(sellerProfile != null
                        ? buildSellerResponse(listing.getSeller(), sellerProfile)
                        : null)
                .createdAt(listing.getCreatedAt())
                .updatedAt(listing.getUpdatedAt())
                .build();
    }

    private UserProfileResponse buildSellerResponse(User seller, UserProfile profile) {
        return UserProfileResponse
                .builder()
                .id(profile.getId())
                .firstName(seller.getFirstName())
                .lastName(seller.getLastName())
                .displayName(seller.getDisplayName())
                .email(seller.getEmail())
                .avatarUrl(profile.getAvatarUrl())
                .bio(profile.getBio())
                .country(profile.getCountry())
                .totalSales(profile.getTotalSales())
                .totalPurchases(profile.getTotalPurchases())
                .rating(profile.getRating())
                .ratingCount(profile.getRatingCount())
                .emailVerified(seller.isEmailVerified())
                .build();
    }
}