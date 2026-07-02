package com.rocketeers.nexus_gold.service;

import com.rocketeers.nexus_gold.dto.game.GameRequest;
import com.rocketeers.nexus_gold.dto.game.GameResponse;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GameService {
    List<GameResponse> getAllActiveGames();

    GameResponse getBySlug(String slug);

    @Transactional
    GameResponse createGame(GameRequest request, MultipartFile icon);

    @Transactional
    void deactiveGame(long gameId);
}
