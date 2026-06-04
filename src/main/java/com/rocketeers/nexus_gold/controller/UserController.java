package com.rocketeers.nexus_gold.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "APIs for user operations")
public class UserController {

    @GetMapping("/dashboard")
    @Operation(summary = "Get user dashboard", description = "Retrieve the user dashboard welcome message")
    @ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully", content = @Content(schema = @Schema(example = "Welcome to the User Dashboard!")))
    public ResponseEntity<String> getAdminDashboard() {
        return ResponseEntity.ok("Welcome to the User Dashboard!");
    }

}
