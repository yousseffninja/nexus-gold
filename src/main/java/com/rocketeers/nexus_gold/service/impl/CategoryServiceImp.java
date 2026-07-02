package com.rocketeers.nexus_gold.service.impl;

import com.rocketeers.nexus_gold.dto.category.CategoryRequest;
import com.rocketeers.nexus_gold.dto.category.CategoryResponse;
import com.rocketeers.nexus_gold.exception.ResourceNotFoundException;
import com.rocketeers.nexus_gold.model.Category;
import com.rocketeers.nexus_gold.model.Game;
import com.rocketeers.nexus_gold.repository.CategoryRepository;
import com.rocketeers.nexus_gold.repository.GameRepository;
import com.rocketeers.nexus_gold.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final GameRepository gameRepository;

    @Override
    public List<CategoryResponse> getCategoriesForGame(long gameId) {
        return categoryRepository.findAllByGameIdAndActiveTrue(gameId)
                .stream()
                .map(category -> toResponse(category, true, "Category retrieve successfully"))
                .toList();
    }

    @Transactional
    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
        Game game = gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new ResourceNotFoundException("Game not found: " + request.getGameId()));

        Category category = new Category();
        category.setName(request.getName());
        category.setType(request.getType());
        category.setGame(game);
        category.setActive(true);

        Category saved = categoryRepository.save(category);

        return toResponse(saved, true, "Category created successfully");
    }

    @Transactional
    @Override
    public void deactiveCategory(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        category.setActive(false);
        categoryRepository.save(category);
    }

    private CategoryResponse toResponse (Category category, boolean success, String message) {
        return CategoryResponse.builder()
                .success(success)
                .message(message)
                .id(category.getId())
                .name(category.getName())
                .type(category.getType())
                .build();
    }

}
