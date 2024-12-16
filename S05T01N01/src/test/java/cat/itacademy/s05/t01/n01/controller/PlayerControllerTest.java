package cat.itacademy.s05.t01.n01.controller;

import cat.itacademy.s05.t01.n01.model.PlayerRanking;
import cat.itacademy.s05.t01.n01.model.dto.playerDto.RankingDto;
import cat.itacademy.s05.t01.n01.model.dto.playerDto.UpdateNameDto;
import cat.itacademy.s05.t01.n01.service.impl.PlayerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerControllerTest {

    @Mock
    private PlayerServiceImpl playerService;

    @InjectMocks
    private PlayerController playerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void Ranking_ReturnsTopPlayers() {
        RankingDto player1 = new RankingDto("Josefina", 10);
        RankingDto player2 = new RankingDto("Eustaqui", 8);
        List<RankingDto> rankingList = Arrays.asList(player1, player2);

        when(playerService.getRanking()).thenReturn(Flux.fromIterable(rankingList));

        Mono<ResponseEntity<List<RankingDto>>> response = playerController.getRanking();

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(entity.getBody()).containsExactly(player1, player2);
                })
                .verifyComplete();

        verify(playerService, times(1)).getRanking();
    }

    @Test
    void updatePlayerName_ReturnsUpdatedPlayer() {
        String playerId = "1";
        UpdateNameDto updateNameDto = new UpdateNameDto("Anacleto");
        PlayerRanking updatedPlayer = new PlayerRanking(playerId, "Anacleto", 5);

        when(playerService.updatePlayerName(playerId, updateNameDto)).thenReturn(Mono.just(updatedPlayer));

        Mono<ResponseEntity<PlayerRanking>> response = playerController.updatePlayerName(playerId, updateNameDto);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(entity.getBody()).isEqualTo(updatedPlayer);
                })
                .verifyComplete();

        verify(playerService, times(1)).updatePlayerName(playerId, updateNameDto);
    }
}
