package com.rocketeers.nexus_gold.repository;

import com.rocketeers.nexus_gold.enums.ListingStatus;
import com.rocketeers.nexus_gold.model.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ListingRepository extends
        JpaRepository<Listing, Long>,
        JpaSpecificationExecutor<Listing> {

    Page<Listing> findAllByStatusAndGameSlug(
            ListingStatus status,
            String gameSlug,
            Pageable pageable
    );

    Page<Listing> findAllByStatusAndSellerDisplayName(
            ListingStatus status,
            String displayName,
            Pageable pageable
    );

    Page<Listing> findAllBySellerId(
            long sellerId,
            Pageable pageable
    );

    @Modifying
    @Query("UPDATE Listing l SET l.viewCount = l.viewCount + 1 WHERE l.id = :id")
    void incrementViewCount(@Param("id") long id);
}