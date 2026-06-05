package com.rocketeers.nexus_gold.service.impl;

import com.rocketeers.nexus_gold.dto.*;
import com.rocketeers.nexus_gold.enums.Roles;
import com.rocketeers.nexus_gold.model.User;
import com.rocketeers.nexus_gold.repository.UserRepository;
import com.rocketeers.nexus_gold.service.AuthenticationService;
import com.rocketeers.nexus_gold.service.EmailService;
import com.rocketeers.nexus_gold.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final EmailService emailService;

    @Value("${app.email.verification.expiration-minutes:15}")
    private long emailVerificationExpirationMinutes;

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
        user.setEmailVerified(false);

        String verificationCode = generateVerificationCode();
        setEmailVerificationCode(user, verificationCode);

        User savedUser = userRepository.save(user);
        String message = "User registered successfully. Verification code sent to email.";

        try {
            emailService.sendVerificationCode(savedUser.getEmail(), verificationCode);
        } catch (MailException e) {
            message = "User registered successfully, but verification email could not be sent. Please use send-verification-code after mail configuration is fixed.";
        }

        return SignUpAuthenticationResponse.builder()
                .success(true)
                .message(message)
                .data(savedUser)
                .build();
    }

    public JwtAuthenticationResponse signIn(SignInRequest signInRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInRequest.getEmail(),
                        signInRequest.getPassword()
                )
        );

        var user = userRepository.findByEmail(
                signInRequest.getEmail()
        ).orElseThrow(
                () -> new IllegalArgumentException("Invalid credentials")
        );

        if (!user.isEmailVerified()) {
            return JwtAuthenticationResponse.builder()
                    .success(false)
                    .message("Please verify your email before signing in")
                    .accessToken(null)
                    .refreshToken(null)
                    .build();
        }

        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        return JwtAuthenticationResponse.builder()
                .success(true)
                .message("Signin successful")
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
                .message("Token refreshed successfully")
                .accessToken(newAccessToken)
                .refreshToken(refreshTokenRequest.getToken())
                .build();
    }

    @Override
    public EmailVerificationResponse sendEmailVerificationCode(EmailVerificationCodeRequest request) {
        String email = normalize(request == null ? null : request.getEmail());
        if (!hasText(email)) {
            return EmailVerificationResponse.builder()
                    .success(false)
                    .message("Email is required")
                    .build();
        }

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return EmailVerificationResponse.builder()
                    .success(false)
                    .message("User not found")
                    .build();
        }

        if (user.isEmailVerified()) {
            return EmailVerificationResponse.builder()
                    .success(true)
                    .message("Email is already verified")
                    .build();
        }

        String verificationCode = generateVerificationCode();
        setEmailVerificationCode(user, verificationCode);
        userRepository.save(user);

        try {
            emailService.sendVerificationCode(user.getEmail(), verificationCode);
        } catch (MailException e) {
            return EmailVerificationResponse.builder()
                    .success(false)
                    .message("Failed to send verification code. Please try again later.")
                    .build();
        }

        return EmailVerificationResponse.builder()
                .success(true)
                .message("Verification code sent to email")
                .build();
    }

    @Override
    public EmailVerificationResponse verifyEmail(VerifyEmailRequest request) {
        String email = normalize(request == null ? null : request.getEmail());
        String code = normalize(request == null ? null : request.getCode());

        if (!hasText(email)) {
            return EmailVerificationResponse.builder()
                    .success(false)
                    .message("Email is required")
                    .build();
        }

        if (!hasText(code)) {
            return EmailVerificationResponse.builder()
                    .success(false)
                    .message("Verification code is required")
                    .build();
        }

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return EmailVerificationResponse.builder()
                    .success(false)
                    .message("User not found")
                    .build();
        }

        if (user.getEmailVerificationCode() == null || user.getEmailVerificationCodeExpiresAt() == null) {
            return EmailVerificationResponse.builder()
                    .success(false)
                    .message("No verification code found. Please request a new code.")
                    .build();
        }

        if (LocalDateTime.now().isAfter(user.getEmailVerificationCodeExpiresAt())) {
            return EmailVerificationResponse.builder()
                    .success(false)
                    .message("Verification code expired. Please request a new code.")
                    .build();
        }

        if (!passwordEncoder.matches(code, user.getEmailVerificationCode())) {
            return EmailVerificationResponse.builder()
                    .success(false)
                    .message("Invalid verification code")
                    .build();
        }

        user.setEmailVerified(true);
        user.setEmailVerificationCode(null);
        user.setEmailVerificationCodeExpiresAt(null);
        userRepository.save(user);

        return EmailVerificationResponse.builder()
                .success(true)
                .message("Email verified successfully")
                .build();
    }

    private String generateVerificationCode() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    private void setEmailVerificationCode(User user, String verificationCode) {
        user.setEmailVerificationCode(passwordEncoder.encode(verificationCode));
        user.setEmailVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(emailVerificationExpirationMinutes));
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }

}
