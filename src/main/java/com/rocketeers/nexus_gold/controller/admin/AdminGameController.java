package com.rocketeers.nexus_gold.controller.admin;

import com.rocketeers.nexus_gold.dto.game.GameRequest;
import com.rocketeers.nexus_gold.dto.game.GameResponse;
import com.rocketeers.nexus_gold.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/games")
@RequiredArgsConstructor
@Tag(name = "Admin - Games", description = "ADMIN-only APIs for managing games")
public class AdminGameController {

    private final GameService gameService;

    @PostMapping
    @Operation(summary = "Create a game", description = "Create a new game (ADMIN only)")
    public ResponseEntity<GameResponse> createGame(@Valid @RequestBody GameRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.createGame(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a game", description = "Soft-delete a game (ADMIN only)")
    public ResponseEntity<Void> deactivateGame(@PathVariable long id) {
        gameService.deactiveGame(id);
        return ResponseEntity.noContent().build();
    }
}