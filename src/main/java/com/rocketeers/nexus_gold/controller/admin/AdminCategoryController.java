package com.rocketeers.nexus_gold.controller.admin;

import com.rocketeers.nexus_gold.dto.category.CategoryRequest;
import com.rocketeers.nexus_gold.dto.category.CategoryResponse;
import com.rocketeers.nexus_gold.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/categories")
@RequiredArgsConstructor
@Tag(name = "Admin - Categories", description = "ADMIN-only APIs for managing categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(summary = "Create a category", description = "Create a new category under a game (ADMIN only)")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a category", description = "Soft-delete a category (ADMIN only)")
    public ResponseEntity<Void> deactivateCategory(@PathVariable long id) {
        categoryService.deactiveCategory(id);
        return ResponseEntity.noContent().build();
    }
}