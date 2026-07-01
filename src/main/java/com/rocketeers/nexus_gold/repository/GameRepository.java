package com.rocketeers.nexus_gold.repository;

import com.rocketeers.nexus_gold.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findBySlugAndActiveTrue(String slug);

    List<Game> findAllByActiveTrue();

    boolean existsBySlug(String slug);

}
