package com.rocketeers.nexus_gold.service.impl;

import com.rocketeers.nexus_gold.dto.authenrication.*;
import com.rocketeers.nexus_gold.dto.email_verfication.EmailVerificationCodeRequest;
import com.rocketeers.nexus_gold.dto.email_verfication.EmailVerificationResponse;
import com.rocketeers.nexus_gold.dto.sign_in.SignInRequest;
import com.rocketeers.nexus_gold.dto.sign_up.SignUpAuthenticationResponse;
import com.rocketeers.nexus_gold.dto.sign_up.SignUpRequest;
import com.rocketeers.nexus_gold.dto.verify_rest_code.VerifyResetCodeRequest;
import com.rocketeers.nexus_gold.dto.verify_rest_code.VerifyResetCodeResponse;
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
import org.springframework.transaction.annotation.Transactional;

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
    private final UserProfileRepository userProfileRepository; // ← NEW

    @Value("${app.email.verification.expiration-minutes:15}")
    private long emailVerificationExpirationMinutes;

    @Value("${app.password-reset.expiration-minutes:15}")
    private long passwordResetExpirationMinutes;

    private Util util = new Util();

    @Transactional
    public SignUpAuthenticationResponse signUp(SignUpRequest signUpRequest) {

        if (userRepository.findByEmail(signUpRequest.getEmail().toLowerCase()).isPresent()) {
            return toSignUpResponse("Email is already in use", false, null);
        }

        if (userRepository.findByDisplayName(signUpRequest.getDisplayName()).isPresent()) {
            return toSignUpResponse("Display name is already in use", false, null);
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

        UserProfile profile = UserProfile.builder()
                .user(savedUser)
                .build();
        userProfileRepository.save(profile);


        verificationCodeRepository.save(verificationCodeEntity);

        String message = "User registered successfully. Verification code sent to email.";

        try {
            emailService.sendVerificationCode(savedUser.getEmail().toLowerCase(), verificationCode);
        } catch (MailException e) {
            message = "User registered successfully, but verification email could not be sent. Please use send-verification-code after mail configuration is fixed.";
        }

        return toSignUpResponse(message, true, savedUser);
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
            return toJwtResponse("Please verify your email before signing in", false, null, null);
        }

        var jwt = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        return toJwtResponse("Signin successful", true, jwt, refreshToken);
    }

    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userEmail = jwtService.extractUserNameFromExpiredToken(refreshTokenRequest.getToken());
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var newAccessToken = jwtService.generateToken(user);
        return toJwtResponse("Token refreshed successfully", true, newAccessToken, refreshTokenRequest.getToken());
    }

    @Override
    public EmailVerificationResponse sendEmailVerificationCode(EmailVerificationCodeRequest request) {
        String email = util.normalize(request == null ? null : request.getEmail().toLowerCase());
        if (!util.hasText(email)) {
            return toEmailVerificationResponse("Email is required", false);
        }

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return toEmailVerificationResponse("User not found", false);
        }

        if (user.isEmailVerified()) {
            return toEmailVerificationResponse("Email is already verified", true);
        }

        String verificationCode = generateVerificationCode();
        VerificationCode verificationCodeEntity = createVerificationCode(user, verificationCode, VerificationCode.VerificationCodeType.EMAIL_VERIFICATION);
        verificationCodeRepository.save(verificationCodeEntity);

        try {
            emailService.sendVerificationCode(user.getEmail().toLowerCase(), verificationCode);
        } catch (MailException e) {
            return toEmailVerificationResponse("Failed to send verification code. Please try again later.", false);
        }

        return toEmailVerificationResponse("Verification code sent to email", true);
    }

    @Override
    public EmailVerificationResponse verifyEmail(VerifyEmailRequest request) {
        String email = util.normalize(request == null ? null : request.getEmail().toLowerCase());
        String code = util.normalize(request == null ? null : request.getCode());

        if (!util.hasText(email)) {
            return toEmailVerificationResponse("Email is required", false);
        }

        if (!util.hasText(code)) {
            return toEmailVerificationResponse("Verification code is required", false);
        }

        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return toEmailVerificationResponse("User not found", false);
        }

        VerificationCode verificationCodeEntity = verificationCodeRepository
                .findByUserAndTypeAndUsedFalse(user, VerificationCode.VerificationCodeType.EMAIL_VERIFICATION)
                .orElse(null);

        if (verificationCodeEntity == null) {
            return toEmailVerificationResponse("No verification code found. Please request a new code.", false);
        }

        if (LocalDateTime.now().isAfter(verificationCodeEntity.getExpiresAt())) {
            verificationCodeEntity.setUsed(true);
            verificationCodeRepository.save(verificationCodeEntity);
            return toEmailVerificationResponse("Verification code expired. Please request a new code.", false);
        }

        if (!passwordEncoder.matches(code, verificationCodeEntity.getCode())) {
            return toEmailVerificationResponse("Invalid verification code", false);
        }

        user.setEmailVerified(true);
        verificationCodeEntity.setUsed(true);
        verificationCodeRepository.save(verificationCodeEntity);
        userRepository.save(user);

        return toEmailVerificationResponse("Email verified successfully", true);
    }

    @Override
    public PasswordResetResponse forgetPassword(ForgetPasswordRequest request){
       String email = util.normalize(request==null ? null : request.getEmail().toLowerCase());

       if (!util.hasText(email)) {

           return toPasswordResetResponse("Email is required", false);
       }

       User user = userRepository.findByEmail(email).orElse(null);

       if (user == null) {
           return toPasswordResetResponse("Email not found", false);
       }

       String resetCode = generateVerificationCode();

       VerificationCode verificationCodeEntity = createVerificationCode(user, resetCode, VerificationCode.VerificationCodeType.PASSWORD_RESET);
       verificationCodeRepository.save(verificationCodeEntity);

       try{
           emailService.sendPasswordResetCode(user.getEmail().toLowerCase(), resetCode);
       } catch (MailException e){
           return toPasswordResetResponse("Failed to send password reset code. Please try again later.", false);

       }

        return toPasswordResetResponse("If an account exists for this email, a password reset code has been sent.", true);

    }

    @Override
    public VerifyResetCodeResponse verifyResetCode(VerifyResetCodeRequest request){
        String email = util.normalize(request == null ? null : request.getEmail().toLowerCase());
        String code = util.normalize(request == null ? null : request.getCode());

        if (!util.hasText(email)) {
            return toVerifyResetCodeResponse("Email is required", false, null);
        }

        if (!util.hasText(code)) {
            return toVerifyResetCodeResponse("Code is required", false, null);
        }

        User user = userRepository.findByEmail(email).orElse(null);
        VerificationCode verificationCodeEntity = verificationCodeRepository
                .findByUserAndTypeAndUsedFalse(user, VerificationCode.VerificationCodeType.PASSWORD_RESET)
                .orElse(null);

        if (user == null || verificationCodeEntity == null) {
            return toVerifyResetCodeResponse("Something went wrong. Please try again later.", false, null);
        }

        if (LocalDateTime.now().isAfter(verificationCodeEntity.getExpiresAt())) {
            verificationCodeEntity.setUsed(true);
            verificationCodeRepository.save(verificationCodeEntity);
            return toVerifyResetCodeResponse("Code has expired. Please request a new code.", false, null);
        }

        if (!passwordEncoder.matches(code, verificationCodeEntity.getCode())) {
            return toVerifyResetCodeResponse("Invalid verification code", false, null);
        }

        String resetToken = generateResetToken();
        verificationCodeEntity.setUsed(true);
        verificationCodeRepository.save(verificationCodeEntity);

        return toVerifyResetCodeResponse("Code verified successfully", true, resetToken);
    }

    @Override
    public PasswordResetResponse resetPassword(ResetPasswordRequest request){
        String email = util.normalize(request == null ? null : request.getEmail());
        String newPassword = util.normalize(request == null ? null : request.getNewPassword());

        if (!util.hasText(email)) {
            return toPasswordResetResponse("Email is required", false);
        }

        if (!util.hasText(newPassword)) {
            return toPasswordResetResponse("New password is required", false);
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return toPasswordResetResponse("User not found", false);
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return toPasswordResetResponse("Password reset successfully", true);
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

    private SignUpAuthenticationResponse toSignUpResponse(String message, boolean success, Object data) {
        return SignUpAuthenticationResponse.builder()
                .message(message)
                .success(success)
                .data(data)
                .build();
    }

    private JwtAuthenticationResponse toJwtResponse(String message, boolean success, String accessToken, String refreshToken) {
        return JwtAuthenticationResponse.builder()
                .message(message)
                .success(success)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private EmailVerificationResponse toEmailVerificationResponse(String message, boolean success) {
        return EmailVerificationResponse.builder()
                .message(message)
                .success(success)
                .build();
    }

    private PasswordResetResponse toPasswordResetResponse(String message, boolean success) {
        return PasswordResetResponse.builder()
                .message(message)
                .success(success)
                .build();
    }

    private VerifyResetCodeResponse toVerifyResetCodeResponse(String message, boolean success, String passwordResetToken) {
        return VerifyResetCodeResponse.builder()
                .message(message)
                .success(success)
                .passwordResetToken(passwordResetToken)
                .build();
    }
}