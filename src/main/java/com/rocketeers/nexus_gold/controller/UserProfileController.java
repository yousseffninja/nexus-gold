package com.rocketeers.nexus_gold.controller;

import com.rocketeers.nexus_gold.dto.profile.UpdateProfileRequest;
import com.rocketeers.nexus_gold.dto.profile.UserProfileResponse;
import com.rocketeers.nexus_gold.model.User;
import com.rocketeers.nexus_gold.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user/profile")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "APIs for managing user profiles")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/me")
    @Operation(summary = "Get my profile", description = "Get the currently authenticated user's profile")
    public ResponseEntity<UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(userProfileService.getMyProfile(currentUser));
    }

    @GetMapping("/{displayName}")
    @SecurityRequirements
    @Operation(summary = "Get profile by display name", description = "Get any user's public profile by display name")
    public ResponseEntity<UserProfileResponse> getProfileByDisplayName(
            @PathVariable String displayName) {
        return ResponseEntity.ok(
                userProfileService.getProfileByDisplayName(displayName)
        );
    }

    @PutMapping(
            value = "/me",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "Update my profile", description = "Update bio, country, and avatar")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @AuthenticationPrincipal User currentUser,
            @ModelAttribute UpdateProfileRequest request) {
        return ResponseEntity.ok(
                userProfileService.updateMyProfile(currentUser, request)
        );
    }
}