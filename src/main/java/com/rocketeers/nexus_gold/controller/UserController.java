package com.rocketeers.nexus_gold.controller;

import com.rocketeers.nexus_gold.dto.change_passoword.ChangePasswordRequest;
import com.rocketeers.nexus_gold.dto.change_passoword.ChangePasswordResponse;
import com.rocketeers.nexus_gold.model.User;
import com.rocketeers.nexus_gold.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User", description = "APIs for user operations")
public class UserController {

    private final UserService userService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get user dashboard", description = "Retrieve the user dashboard welcome message")
    @ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully", content = @Content(schema = @Schema(example = "Welcome to the User Dashboard!")))
    public ResponseEntity<String> getAdminDashboard() {
        return ResponseEntity.ok("Welcome to the User Dashboard!");
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change password", description = "Change the authenticated user's password using a bearer token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully", content = @Content(schema = @Schema(implementation = ChangePasswordResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid password change request"),
            @ApiResponse(responseCode = "401", description = "Bearer token is missing or invalid")
    })
    public ResponseEntity<ChangePasswordResponse> changePassword(
            @AuthenticationPrincipal User user,
            @RequestBody ChangePasswordRequest request
    ) {
        ChangePasswordResponse response = userService.changePassword(user, request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        return ResponseEntity.badRequest().body(response);
    }

}
