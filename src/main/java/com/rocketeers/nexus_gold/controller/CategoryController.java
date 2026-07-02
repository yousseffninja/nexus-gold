package com.rocketeers.nexus_gold.controller;

import com.rocketeers.nexus_gold.dto.category.CategoryResponse;
import com.rocketeers.nexus_gold.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/games/{gameId}/categories")
@RequiredArgsConstructor
@SecurityRequirements
@Tag(name = "Categories", description = "Public APIs for browsing categories within a game")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "List categories for a game", description = "Get all active categories (Gold, Items, Accounts, Boosting) for a given game")
    public ResponseEntity<List<CategoryResponse>> getCategories(@PathVariable long gameId) {
        return ResponseEntity.ok(categoryService.getCategoriesForGame(gameId));
    }
}