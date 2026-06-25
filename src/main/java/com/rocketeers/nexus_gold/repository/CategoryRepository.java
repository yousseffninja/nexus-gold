package com.rocketeers.nexus_gold.repository;

import com.rocketeers.nexus_gold.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByGameIdAndActiveTrue(String gameId);

}
