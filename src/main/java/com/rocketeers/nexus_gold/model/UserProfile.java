package com.rocketeers.nexus_gold.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(length = 500)
    private String bio;

    @Column(length = 100)
    private String country;

    @Builder.Default
    @Column(name = "total_sales", nullable = false)
    private int totalSales = 0;

    @Builder.Default
    @Column(name = "total_purchases", nullable = false)
    private int totalPurchases = 0;

    @Builder.Default
    @Column(nullable = false)
    private double rating = 0.0;

    @Builder.Default
    @Column(name = "rating_count", nullable = false)
    private int ratingCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}