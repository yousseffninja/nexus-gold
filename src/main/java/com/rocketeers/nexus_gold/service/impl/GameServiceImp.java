package com.rocketeers.nexus_gold.service.impl;

import com.rocketeers.nexus_gold.dto.game.GameRequest;
import com.rocketeers.nexus_gold.dto.game.GameResponse;
import com.rocketeers.nexus_gold.exception.ConflictException;
import com.rocketeers.nexus_gold.exception.ResourceNotFoundException;
import com.rocketeers.nexus_gold.model.Game;
import com.rocketeers.nexus_gold.repository.GameRepository;
import com.rocketeers.nexus_gold.service.CloudinaryService;
import com.rocketeers.nexus_gold.service.GameService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GameServiceImp implements GameService {

    private final GameRepository gameRepository;
    private final CloudinaryService cloudinaryService;


    @Override
    public List<GameResponse> getAllActiveGames() {
        return gameRepository.findAllByActiveTrue()
                .stream()
                .map(game -> toResponse(game, "Games retrieved successfully", true))
                .toList();
    }

    @Override
    public GameResponse getBySlug(String slug) {
        return gameRepository.findBySlugAndActiveTrue(slug)
                .map(game -> toResponse(game, "Game retrieved successfully", true))
                .orElseThrow(() -> new ResourceNotFoundException("Game not found: " + slug));
    }

    @Transactional
    @Override
    public GameResponse createGame(GameRequest request, MultipartFile icon) {
        if (gameRepository.existsBySlug(request.getSlug())) {
            throw new ConflictException("Game with slug " + request.getSlug() + " already exists");
        }

        String iconUrl = cloudinaryService.uploadImage(icon, "games");

        Game game = new Game();
        game.setName(request.getName());
        game.setSlug(request.getSlug());
        game.setIconUrl(iconUrl);
        game.setActive(true);

        Game saved = gameRepository.save(game);

        return toResponse(saved, "Game created successfully", true);
    }

    @Transactional
    @Override
    public void deactiveGame(long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found: " + gameId));
        game.setActive(false);
        gameRepository.save(game);
    }

    private GameResponse toResponse(Game game, String message, boolean success){
        return GameResponse.builder()
                .id(game.getId())
                .name(game.getName())
                .slug(game.getSlug())
                .iconUrl(game.getIconUrl())
                .message(message)
                .success(success)
                .build();
    }
}
