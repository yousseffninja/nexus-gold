package com.rocketeers.nexus_gold.controller;

import com.rocketeers.nexus_gold.dto.authenrication.*;
import com.rocketeers.nexus_gold.dto.email_verfication.EmailVerificationCodeRequest;
import com.rocketeers.nexus_gold.dto.email_verfication.EmailVerificationResponse;
import com.rocketeers.nexus_gold.dto.sign_in.SignInRequest;
import com.rocketeers.nexus_gold.dto.sign_up.SignUpAuthenticationResponse;
import com.rocketeers.nexus_gold.dto.sign_up.SignUpRequest;
import com.rocketeers.nexus_gold.dto.verify_rest_code.VerifyResetCodeRequest;
import com.rocketeers.nexus_gold.dto.verify_rest_code.VerifyResetCodeResponse;
import com.rocketeers.nexus_gold.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@SecurityRequirements
@Tag(name = "Authentication", description = "APIs for user authentication including signup, signin, and token refresh")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    @Operation(summary = "User signup", description = "Register a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signup successful", content = @Content(schema = @Schema(implementation = SignUpAuthenticationResponse.class))),
            @ApiResponse(responseCode = "409", description = "User already exists")
    })
    public ResponseEntity<SignUpAuthenticationResponse> signup (@RequestBody SignUpRequest signUpRequest) {
        SignUpAuthenticationResponse response = authenticationService.signUp(signUpRequest);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @PostMapping("/signin")
    @Operation(summary = "User signin", description = "Authenticate user and return JWT tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Signin successful", content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    public ResponseEntity<JwtAuthenticationResponse> signin (@RequestBody SignInRequest signInRequest) {
        try {
            JwtAuthenticationResponse response = authenticationService.signIn(signInRequest);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (AuthenticationException | IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    JwtAuthenticationResponse.builder()
                            .success(false)
                            .message("Invalid credentials")
                            .accessToken(null)
                            .refreshToken(null)
                            .build()
            );
        }
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Refresh the JWT access token using a valid refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refresh successful", content = @Content(schema = @Schema(implementation = JwtAuthenticationResponse.class))),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    public ResponseEntity<JwtAuthenticationResponse> refresh (@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            JwtAuthenticationResponse response = authenticationService.refreshToken(refreshTokenRequest);
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    JwtAuthenticationResponse.builder()
                            .success(false)
                            .message("Invalid or expired refresh token")
                            .accessToken(null)
                            .refreshToken(null)
                            .build()
            );
        }
    }

    @PostMapping("/send-verification-code")
    @Operation(summary = "Send email verification code", description = "Send a new email verification code to a registered user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Verification code sent", content = @Content(schema = @Schema(implementation = EmailVerificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or email could not be sent")
    })
    public ResponseEntity<EmailVerificationResponse> sendVerificationCode(@RequestBody EmailVerificationCodeRequest request) {
        EmailVerificationResponse response = authenticationService.sendEmailVerificationCode(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/verify-email")
    @Operation(summary = "Verify email", description = "Verify a registered user's email using the code sent to Gmail")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email verified", content = @Content(schema = @Schema(implementation = EmailVerificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired verification code")
    })
    public ResponseEntity<EmailVerificationResponse> verifyEmail(@RequestBody VerifyEmailRequest request) {
        EmailVerificationResponse response = authenticationService.verifyEmail(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.badRequest().body(response);
    }



    @PostMapping("/forget-password")
    @Operation(summary = "forget password", description = "Send a password reset code to the user's email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset code sent if account exists", content = @Content(schema = @Schema(implementation = PasswordResetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or email could not be sent")
    })
    public ResponseEntity<PasswordResetResponse> forgetPassword( @RequestBody ForgetPasswordRequest request) {

        PasswordResetResponse response = authenticationService.forgetPassword(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);

    }

    @PostMapping("/verify-reset-code")
    @Operation(summary = "Verify reset code", description = "Verify the password reset code and generate a token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Code verified successfully", content = @Content(schema = @Schema(implementation = VerifyResetCodeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid or expired code")
    })
    public ResponseEntity<VerifyResetCodeResponse> verifyResetCode(@RequestBody VerifyResetCodeRequest request) {

        VerifyResetCodeResponse response = authenticationService.verifyResetCode(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);

    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset a user's password using the token obtained from verify-reset-code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully", content = @Content(schema = @Schema(implementation = PasswordResetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request or token")
    })
    public ResponseEntity<PasswordResetResponse> resetPassword( @RequestBody ResetPasswordRequest request) {

        PasswordResetResponse response = authenticationService.resetPassword(request);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(response);

    }

}
