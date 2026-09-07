package com.rocketeers.nexus_gold.dto.profile;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponse {
    private long id;
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private String avatarUrl;
    private String bio;
    private String country;
    private int totalSales;
    private int totalPurchases;
    private double rating;
    private int ratingCount;
    private boolean emailVerified;
}