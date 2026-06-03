package com.rocketeers.nexus_gold.controller;

import com.rocketeers.nexus_gold.dto.*;
import com.rocketeers.nexus_gold.service.AuthenticationService;
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
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpAuthenticationResponse> signup (@RequestBody SignUpRequest signUpRequest) {
        SignUpAuthenticationResponse response = authenticationService.signUp(signUpRequest);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @PostMapping("/signin")
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

