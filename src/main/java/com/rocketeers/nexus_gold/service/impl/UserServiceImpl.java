package com.rocketeers.nexus_gold.service.impl;

import com.rocketeers.nexus_gold.dto.change_passoword.ChangePasswordRequest;
import com.rocketeers.nexus_gold.dto.change_passoword.ChangePasswordResponse;
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
            return toChangePasswordResponse("Authenticated user is required", false);
        }

        if (request == null || !util.hasText(request.getCurrentPassword())) {
            return toChangePasswordResponse("Current password is required", false);
        }

        if (!util.hasText(request.getNewPassword())) {
            return toChangePasswordResponse("New password is required", false);
        }

        if (request.getNewPassword().length() < 8) {
            return toChangePasswordResponse("New password must be at least 8 characters", false);
        }

        User managedUser = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), managedUser.getPassword())) {
            return toChangePasswordResponse("Current password is incorrect", false);
        }

        if (passwordEncoder.matches(request.getNewPassword(), managedUser.getPassword())) {
            return toChangePasswordResponse("New password must be different from current password", false);
        }

        managedUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(managedUser);

        return toChangePasswordResponse("Password changed successfully", true);
    }

    private ChangePasswordResponse toChangePasswordResponse(String message, boolean success) {
        return ChangePasswordResponse.builder()
                .message(message)
                .success(success)
                .build();
    }

}
