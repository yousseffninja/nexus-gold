package com.rocketeers.nexus_gold.service;

import com.rocketeers.nexus_gold.dto.profile.UpdateProfileRequest;
import com.rocketeers.nexus_gold.dto.profile.UserProfileResponse;
import com.rocketeers.nexus_gold.model.User;
import jakarta.transaction.Transactional;

public interface UserProfileService {

    UserProfileResponse getMyProfile(User currentUser);

    UserProfileResponse getProfileByDisplayName(String displayName);

    @Transactional
    UserProfileResponse updateMyProfile(User currentUser, UpdateProfileRequest request);

}
