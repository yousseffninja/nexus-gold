package com.rocketeers.nexus_gold.service;

import com.rocketeers.nexus_gold.dto.category.CategoryRequest;
import com.rocketeers.nexus_gold.dto.category.CategoryResponse;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getCategoriesForGame(long gameId);

    @Transactional
    CategoryResponse createCategory(CategoryRequest request);

    @Transactional
    void deactiveCategory(long id);
}
