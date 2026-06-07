package com.rocketeers.nexus_gold.service.impl;

import com.rocketeers.nexus_gold.dto.ChangePasswordRequest;
import com.rocketeers.nexus_gold.dto.ChangePasswordResponse;
import com.rocketeers.nexus_gold.model.User;
import com.rocketeers.nexus_gold.repository.UserRepository;
import com.rocketeers.nexus_gold.service.UserService;
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
        if (user == null) {
            return ChangePasswordResponse.builder()
                    .success(false)
                    .message("Authenticated user is required")
                    .build();
        }

        if (request == null || !hasText(request.getCurrentPassword())) {
            return ChangePasswordResponse.builder()
                    .success(false)
                    .message("Current password is required")
                    .build();
        }

        if (!hasText(request.getNewPassword())) {
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

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

}
