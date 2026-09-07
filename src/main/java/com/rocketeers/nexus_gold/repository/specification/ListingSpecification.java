package com.rocketeers.nexus_gold.repository.specification;

import com.rocketeers.nexus_gold.enums.DeliveryMethod;
import com.rocketeers.nexus_gold.enums.ListingStatus;
import com.rocketeers.nexus_gold.model.Listing;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ListingSpecification {

    // Private constructor — this is a utility class, not meant to be instantiated
    private ListingSpecification() {}

    public static Specification<Listing> filter(
            String gameSlug,
            String categoryType,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            DeliveryMethod deliveryMethod,
            Double minSellerRating,
            String search
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Always only show ACTIVE listings in public search
            predicates.add(cb.equal(
                    root.get("status"), ListingStatus.ACTIVE
            ));

            // Filter by game slug
            if (gameSlug != null && !gameSlug.isBlank()) {
                predicates.add(cb.equal(
                        root.get("game").get("slug"), gameSlug
                ));
            }

            // Filter by category type
            if (categoryType != null && !categoryType.isBlank()) {
                predicates.add(cb.equal(
                        root.get("category").get("type").as(String.class),
                        categoryType.toUpperCase()
                ));
            }

            // Filter by min price
            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("price"), minPrice
                ));
            }

            // Filter by max price
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(
                        root.get("price"), maxPrice
                ));
            }

            // Filter by delivery method
            if (deliveryMethod != null) {
                predicates.add(cb.equal(
                        root.get("deliveryMethod"), deliveryMethod
                ));
            }

            // Filter by minimum seller rating
            if (minSellerRating != null) {
                predicates.add(cb.greaterThanOrEqualTo(
                        root.get("seller")
                                .get("userProfile")
                                .get("rating"),
                        minSellerRating
                ));
            }

            // Keyword search in title and description
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}