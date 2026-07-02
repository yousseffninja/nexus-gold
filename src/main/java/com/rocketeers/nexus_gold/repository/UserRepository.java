package com.rocketeers.nexus_gold.repository;

import com.rocketeers.nexus_gold.model.Role;
import com.rocketeers.nexus_gold.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByDisplayName(String displayName);

    List<User> findByRole(Role role);
}
