package com.rocketeers.nexus_gold.repository;

import com.rocketeers.nexus_gold.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Optional<UserProfile> findByUserId(long userId);

    Optional<UserProfile> findByUserEmail(String email);

    Optional<UserProfile> findByUserDisplayName(String displayName);
}