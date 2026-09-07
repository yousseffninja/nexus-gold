package com.rocketeers.nexus_gold.service.impl;

import com.rocketeers.nexus_gold.dto.profile.UpdateProfileRequest;
import com.rocketeers.nexus_gold.dto.profile.UserProfileResponse;
import com.rocketeers.nexus_gold.exception.ResourceNotFoundException;
import com.rocketeers.nexus_gold.model.User;
import com.rocketeers.nexus_gold.model.UserProfile;
import com.rocketeers.nexus_gold.repository.UserProfileRepository;
import com.rocketeers.nexus_gold.service.CloudinaryService;
import com.rocketeers.nexus_gold.service.UserProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImp implements UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final CloudinaryService cloudinaryService;

    public UserProfileResponse getMyProfile(User currentUser) {
        UserProfile profile = userProfileRepository
                .findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profile not found for user: " + currentUser.getEmail()
                ));
        return toResponse(currentUser, profile);
    }

    public UserProfileResponse getProfileByDisplayName(String displayName) {
        UserProfile profile = userProfileRepository
                .findByUserDisplayName(displayName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profile not found: " + displayName
                ));
        return toResponse(profile.getUser(), profile);
    }

    @Transactional
    public UserProfileResponse updateMyProfile(
            User currentUser,
            UpdateProfileRequest request) {

        UserProfile profile = userProfileRepository
                .findByUserId(currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Profile not found"
                ));

        // Update text fields if provided
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getCountry() != null) {
            profile.setCountry(request.getCountry());
        }

        // Upload new avatar if provided
        if (request.getAvatar() != null && !request.getAvatar().isEmpty()) {
            String avatarUrl = cloudinaryService.uploadImage(
                    request.getAvatar(),
                    "nexusgold/avatars"
            );
            profile.setAvatarUrl(avatarUrl);
        }

        UserProfile saved = userProfileRepository.save(profile);
        return toResponse(currentUser, saved);
    }

    private UserProfileResponse toResponse(User user, UserProfile profile) {
        return UserProfileResponse.builder()
                .id(profile.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .avatarUrl(profile.getAvatarUrl())
                .bio(profile.getBio())
                .country(profile.getCountry())
                .totalSales(profile.getTotalSales())
                .totalPurchases(profile.getTotalPurchases())
                .rating(profile.getRating())
                .ratingCount(profile.getRatingCount())
                .emailVerified(user.isEmailVerified())
                .build();
    }

}
