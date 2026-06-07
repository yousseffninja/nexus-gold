package com.rocketeers.nexus_gold.service.impl;

import com.rocketeers.nexus_gold.dto.ChangePasswordRequest;
import com.rocketeers.nexus_gold.dto.ChangePasswordResponse;
import com.rocketeers.nexus_gold.model.User;
import com.rocketeers.nexus_gold.repository.UserRepository;
import com.rocketeers.nexus_gold.service.UserService;
import com.rocketeers.nexus_gold.service.Util;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private Util util = new Util();

    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return userRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }

    @Override
    public ChangePasswordResponse changePassword(User user, ChangePasswordRequest request) {
        String currentPassword = util.normalize(request == null ? null :request.getCurrentPassword());
        String newPassword = util.normalize(request == null ? null :request.getNewPassword());

        if (user == null) {
            return ChangePasswordResponse.builder()
                    .success(false)
                    .message("Authenticated user is required")
                    .build();
        }

        if (request == null || !util.hasText(request.getCurrentPassword())) {
            return ChangePasswordResponse.builder()
                    .success(false)
                    .message("Current password is required")
                    .build();
        }

        if (!util.hasText(request.getNewPassword())) {
            return ChangePasswordResponse.builder()
                    .success(false)
                    .message("New password is required")
                    .build();
        }

        if (request.getNewPassword().length() < 8) {
            return ChangePasswordResponse.builder()
                    .success(false)
                    .message("New password must be at least 8 characters")
                    .build();
        }

        User managedUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), managedUser.getPassword())) {
            return ChangePasswordResponse.builder()
                    .success(false)
                    .message("Current password is incorrect")
                    .build();
        }

        if (passwordEncoder.matches(request.getNewPassword(), managedUser.getPassword())) {
            return ChangePasswordResponse.builder()
                    .success(false)
                    .message("New password must be different from current password")
                    .build();
        }

        managedUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(managedUser);

        return ChangePasswordResponse.builder()
                .success(true)
                .message("Password changed successfully")
                .build();
    }



}
