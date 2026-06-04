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
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "APIs for admin operations")
public class AdminController {

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard", description = "Retrieve the admin dashboard welcome message")
    @ApiResponse(responseCode = "200", description = "Dashboard data retrieved successfully", content = @Content(schema = @Schema(example = "Welcome to the Admin Dashboard!")))
    public ResponseEntity<String> getAdminDashboard() {
        return ResponseEntity.ok("Welcome to the Admin Dashboard!");
    }

}
