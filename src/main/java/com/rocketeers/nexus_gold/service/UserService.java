package com.rocketeers.nexus_gold.service;

import com.rocketeers.nexus_gold.dto.ChangePasswordRequest;
import com.rocketeers.nexus_gold.dto.ChangePasswordResponse;
import com.rocketeers.nexus_gold.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();

    ChangePasswordResponse changePassword(User user, ChangePasswordRequest request);
}
