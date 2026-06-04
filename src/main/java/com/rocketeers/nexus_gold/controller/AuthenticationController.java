package com.rocketeers.nexus_gold.controller;

import com.rocketeers.nexus_gold.dto.*;
import com.rocketeers.nexus_gold.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
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
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    JwtAuthenticationResponse.builder()
                            .success(false)
                            .accessToken(null)
                            .refreshToken(null)
                            .build()
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    JwtAuthenticationResponse.builder()
                            .success(false)
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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    JwtAuthenticationResponse.builder()
                            .success(false)
                            .accessToken(null)
                            .refreshToken(null)
                            .build()
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    JwtAuthenticationResponse.builder()
                            .success(false)
                            .accessToken(null)
                            .refreshToken(null)
                            .build()
            );
        }
    }

}

