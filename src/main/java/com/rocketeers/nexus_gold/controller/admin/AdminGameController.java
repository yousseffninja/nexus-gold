package com.rocketeers.nexus_gold.controller.admin;

import com.rocketeers.nexus_gold.dto.game.GameRequest;
import com.rocketeers.nexus_gold.dto.game.GameResponse;
import com.rocketeers.nexus_gold.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/games")
@RequiredArgsConstructor
@Tag(name = "Admin - Games", description = "ADMIN-only APIs for managing games")
public class AdminGameController {

    private final GameService gameService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a game", description = "Create a new game with its icon image (ADMIN only)")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = {
                            @Encoding(name = "request", contentType = MediaType.APPLICATION_JSON_VALUE)
                    }
            )
    )
    public ResponseEntity<GameResponse> createGame(
            @Valid @RequestPart("request") GameRequest request,
            @RequestPart("icon") MultipartFile icon
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(gameService.createGame(request, icon));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate a game", description = "Soft-delete a game (ADMIN only)")
    public ResponseEntity<Void> deactivateGame(@PathVariable long id) {
        gameService.deactiveGame(id);
        return ResponseEntity.noContent().build();
    }
}