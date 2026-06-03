package com.rocketeers.nexus_gold.service.impl;

import com.rocketeers.nexus_gold.dto.*;
import com.rocketeers.nexus_gold.enums.Roles;
import com.rocketeers.nexus_gold.model.User;
import com.rocketeers.nexus_gold.repository.UserRepository;
import com.rocketeers.nexus_gold.service.AuthenticationService;
import com.rocketeers.nexus_gold.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public SignUpAuthenticationResponse signUp(SignUpRequest signUpRequest) {
        
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            return SignUpAuthenticationResponse.builder()
                    .success(false)
                    .message("Email is already in use")
                    .data(null)
                    .build();
        }

        if (userRepository.findByDisplayName(signUpRequest.getDisplayName()).isPresent()) {
            return SignUpAuthenticationResponse.builder()
                    .success(false)
                    .message("Display name is already in use")
                    .data(null)
                    .build();
        }

        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setDisplayName(signUpRequest.getDisplayName());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(Roles.USER);
        
        User savedUser = userRepository.save(user);
        
        return SignUpAuthenticationResponse.builder()
                .success(true)
                .message("User registered successfully")
                .data(savedUser)
                .build();
    }

    public JwtAuthenticationResponse signIn(SignInRequest SignInRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        SignInRequest.getEmail(),
                        SignInRequest.getPassword()
                )
        );

        var user = userRepository.findByEmail(
                SignInRequest.getEmail()
        ).orElseThrow(
                () -> new IllegalArgumentException("Invalid credentials")
        );

        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        return JwtAuthenticationResponse.builder()
                .success(true)
                .accessToken(jwt)
                .refreshToken(refreshToken)
                .build();
    }

    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userEmail = jwtService.extractUserNameFromExpiredToken(refreshTokenRequest.getToken());
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var newAccessToken = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder()
                .success(true)
                .accessToken(newAccessToken)
                .refreshToken(refreshTokenRequest.getToken())
                .build();
    }

}

