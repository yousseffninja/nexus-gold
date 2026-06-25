package com.rocketeers.nexus_gold.controller;

import com.rocketeers.nexus_gold.dto.game.GameResponse;
import com.rocketeers.nexus_gold.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/games")
@RequiredArgsConstructor
@SecurityRequirements
@Tag(name = "Games", description = "Public APIs for browsing available games")
public class GameController {

    private final GameService gameService;

    @GetMapping
    @Operation(summary = "List all games", description = "Get all active games available on the marketplace")
    public ResponseEntity<List<GameResponse>> getAllGames() {
        return ResponseEntity.ok(gameService.getAllActiveGames());
    }

    @GetMapping("/{slug}")
    @Operation(summary = "Get game by slug", description = "Get a single game's details by its slug")
    public ResponseEntity<GameResponse> getGameBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(gameService.getBySlug(slug));
    }
}