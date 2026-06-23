package com.rocketeers.nexus_gold.service.impl;

import com.rocketeers.nexus_gold.dto.*;
import com.rocketeers.nexus_gold.model.*;
import com.rocketeers.nexus_gold.repository.*;
import com.rocketeers.nexus_gold.service.AuthenticationService;
import com.rocketeers.nexus_gold.service.EmailService;
import com.rocketeers.nexus_gold.service.JwtService;
import com.rocketeers.nexus_gold.service.Util;
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

    private final RoleRepository roleRepository;

    private final VerificationCodeRepository verificationCodeRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.email.verification.expiration-minutes:15}")
    private long emailVerificationExpirationMinutes;

    @Value("${app.password-reset.expiration-minutes:15}")
    private long passwordResetExpirationMinutes;

    private Util util = new Util();

    public SignUpAuthenticationResponse signUp(SignUpRequest signUpRequest) {

        if (userRepository.findByEmail(signUpRequest.getEmail().toLowerCase()).isPresent()) {
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

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("USER role not found"));

        User user = new User();
        user.setEmail(signUpRequest.getEmail().toLowerCase());
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setDisplayName(signUpRequest.getDisplayName());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(userRole);
        user.setEmailVerified(false);

        String verificationCode = generateVerificationCode();
        VerificationCode verificationCodeEntity = createVerificationCode(user, verificationCode, VerificationCode.VerificationCodeType.EMAIL_VERIFICATION);

        User savedUser = userRepository.save(user);
        verificationCodeRepository.save(verificationCodeEntity);
        String message = "User registered successfully. Verification code sent to email.";

        try {
            emailService.sendVerificationCode(savedUser.getEmail().toLowerCase(), verificationCode);
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
                        signInRequest.getEmail().toLowerCase(),
                        signInRequest.getPassword()
                )
        );

        var user = userRepository.findByEmail(
                signInRequest.getEmail().toLowerCase()
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
        String email = util.normalize(request == null ? null : request.getEmail().toLowerCase());
        if (!util.hasText(email)) {
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
        VerificationCode verificationCodeEntity = createVerificationCode(user, verificationCode, VerificationCode.VerificationCodeType.EMAIL_VERIFICATION);
        verificationCodeRepository.save(verificationCodeEntity);

        try {
            emailService.sendVerificationCode(user.getEmail().toLowerCase(), verificationCode);
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
        String email = util.normalize(request == null ? null : request.getEmail().toLowerCase());
        String code = util.normalize(request == null ? null : request.getCode());

        if (!util.hasText(email)) {
            return EmailVerificationResponse.builder()
                    .success(false)
                    .message("Email is required")
                    .build();
        }

        if (!util.hasText(code)) {
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

        VerificationCode verificationCodeEntity = verificationCodeRepository
                .findByUserAndTypeAndUsedFalse(user, VerificationCode.VerificationCodeType.EMAIL_VERIFICATION)
                .orElse(null);

        if (verificationCodeEntity == null) {
            return EmailVerificationResponse.builder()
                    .success(false)
                    .message("No verification code found. Please request a new code.")
                    .build();
        }

        if (LocalDateTime.now().isAfter(verificationCodeEntity.getExpiresAt())) {
            verificationCodeEntity.setUsed(true);
            verificationCodeRepository.save(verificationCodeEntity);
            return EmailVerificationResponse.builder()
                    .success(false)
                    .message("Verification code expired. Please request a new code.")
                    .build();
        }

        if (!passwordEncoder.matches(code, verificationCodeEntity.getCode())) {
            return EmailVerificationResponse.builder()
                    .success(false)
                    .message("Invalid verification code")
                    .build();
        }

        user.setEmailVerified(true);
        verificationCodeEntity.setUsed(true);
        verificationCodeRepository.save(verificationCodeEntity);
        userRepository.save(user);

        return EmailVerificationResponse.builder()
                .success(true)
                .message("Email verified successfully")
                .build();
    }

    @Override
    public PasswordResetResponse forgetPassword(ForgetPasswordRequest request){
       String email = util.normalize(request==null ? null : request.getEmail().toLowerCase());

       if (!util.hasText(email)) {

           return  PasswordResetResponse.builder()
                   .success(false).message(" Email is require").build();
       }

       User user = userRepository.findByEmail(email).orElse(null);

       if (user == null) {
           return PasswordResetResponse.builder()
                   .success(false).message("Email not found").build();
       }

       String resetCode = generateVerificationCode();

       VerificationCode verificationCodeEntity = createVerificationCode(user, resetCode, VerificationCode.VerificationCodeType.PASSWORD_RESET);
       verificationCodeRepository.save(verificationCodeEntity);

       try{
           emailService.sendPasswordResetCode(user.getEmail().toLowerCase(), resetCode);
       } catch (MailException e){
           return PasswordResetResponse.builder().success(false).message("Failed to send password reset code. Please try again later.").build();

       }

        return  PasswordResetResponse.builder().success(true).message("If an account exists for this email, a password reset code has been sent.").build();

    }

    @Override
    public VerifyResetCodeResponse verifyResetCode(VerifyResetCodeRequest request){
        String email = util.normalize(request == null ? null : request.getEmail().toLowerCase());
        String code = util.normalize(request == null ? null : request.getCode());

        if (!util.hasText(email)) {
            return VerifyResetCodeResponse.builder()
                    .success(false).message("Email is required").build();
        }

        if (!util.hasText(code)) {
            return VerifyResetCodeResponse.builder()
                    .success(false).message("Code is required").build();
        }

        User user = userRepository.findByEmail(email).orElse(null);
        VerificationCode verificationCodeEntity = verificationCodeRepository
                .findByUserAndTypeAndUsedFalse(user, VerificationCode.VerificationCodeType.PASSWORD_RESET)
                .orElse(null);

        if (user == null || verificationCodeEntity == null) {
            return VerifyResetCodeResponse.builder().success(false).message("Something went wrong. Please try again later.").build();
        }

        if (LocalDateTime.now().isAfter(verificationCodeEntity.getExpiresAt())) {
            verificationCodeEntity.setUsed(true);
            verificationCodeRepository.save(verificationCodeEntity);
            return VerifyResetCodeResponse.builder().success(false).message("Code has expired. Please request a new code.").build();
        }

        if (!passwordEncoder.matches(code, verificationCodeEntity.getCode())) {
            return VerifyResetCodeResponse.builder().success(false).message("Invalid verification code").build();
        }

        String resetToken = generateResetToken();
        verificationCodeEntity.setUsed(true);
        verificationCodeRepository.save(verificationCodeEntity);

        return VerifyResetCodeResponse.builder().success(true).message("Code verified successfully").passwordResetToken(resetToken).build();
    }


    @Override
    public PasswordResetResponse resetPassword(ResetPasswordRequest request){
        String email = util.normalize(request == null ? null : request.getEmail());
        String newPassword = util.normalize(request == null ? null : request.getNewPassword());

        if (!util.hasText(email)) {
            return PasswordResetResponse.builder()
                    .success(false).message("Email is required").build();
        }

        if (!util.hasText(newPassword)) {
            return PasswordResetResponse.builder()
                    .success(false).message("New password is required").build();
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return PasswordResetResponse.builder().success(false).message("User not found").build();
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return PasswordResetResponse.builder().success(true).message("Password reset successfully").build();
    }



    private String generateVerificationCode() {
        return String.format("%06d", SECURE_RANDOM.nextInt(1_000_000));
    }

    private String generateResetToken() {
        return java.util.UUID.randomUUID().toString();
    }

    private VerificationCode createVerificationCode(User user, String code, VerificationCode.VerificationCodeType type) {
        return VerificationCode.builder()
                .code(passwordEncoder.encode(code))
                .type(type)
                .user(user)
                .expiresAt(LocalDateTime.now().plusMinutes(
                        type == VerificationCode.VerificationCodeType.EMAIL_VERIFICATION 
                                ? emailVerificationExpirationMinutes 
                                : passwordResetExpirationMinutes))
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();
    }



}
