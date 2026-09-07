package com.rocketeers.nexus_gold.repository;

import com.rocketeers.nexus_gold.model.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingImageRepository extends JpaRepository<ListingImage, Long> {

    List<ListingImage> findAllByListingIdOrderByDisplayOrderAsc(long listingId);

    void deleteAllByListingId(long listingId);
}