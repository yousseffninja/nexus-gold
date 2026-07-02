package com.rocketeers.nexus_gold.service;

import com.rocketeers.nexus_gold.dto.change_passoword.ChangePasswordRequest;
import com.rocketeers.nexus_gold.dto.change_passoword.ChangePasswordResponse;
import com.rocketeers.nexus_gold.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {
    UserDetailsService userDetailsService();

    ChangePasswordResponse changePassword(User user, ChangePasswordRequest request);
}
