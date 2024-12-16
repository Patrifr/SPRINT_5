package cat.itacademy.s05.t01.n01.service;

import cat.itacademy.s05.t01.n01.exception.custom.PlayerNotFoundException;
import cat.itacademy.s05.t01.n01.model.PlayerRanking;
import cat.itacademy.s05.t01.n01.model.dto.playerDto.RankingDto;
import cat.itacademy.s05.t01.n01.model.dto.playerDto.UpdateNameDto;
import cat.itacademy.s05.t01.n01.repository.PlayerRepository;
import cat.itacademy.s05.t01.n01.service.impl.PlayerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;

import static org.mockito.Mockito.*;

public class PlayerServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerServiceImpl playerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void RankingReturnsTop10Players() {
        PlayerRanking player1 = new PlayerRanking("1", "Joselito", 5);
        PlayerRanking player2 = new PlayerRanking("2", "Eustaqui", 8);
        PlayerRanking player3 = new PlayerRanking("3", "Josefina", 10);

        when(playerRepository.findAll()).thenReturn(Flux.fromIterable(Arrays.asList(player1, player2, player3)));

        Flux<RankingDto> result = playerService.getRanking();

        StepVerifier.create(result)
                .expectNextMatches(dto -> dto.getName().equals("Josefina") && dto.getWins() == 10)
                .expectNextMatches(dto -> dto.getName().equals("Eustaqui") && dto.getWins() == 8)
                .expectNextMatches(dto -> dto.getName().equals("Joselito") && dto.getWins() == 5)
                .verifyComplete();

        verify(playerRepository, times(1)).findAll();
    }

    @Test
    void updatePlayerName_ThrowsPlayerNotFoundException() {
        String playerId = "1";
        UpdateNameDto updateNameDto = new UpdateNameDto("Anacleto");

        when(playerRepository.findById(playerId)).thenReturn(Mono.empty());

        Mono<PlayerRanking> result = playerService.updatePlayerName(playerId, updateNameDto);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof PlayerNotFoundException &&
                        throwable.getMessage().equals("Player with id: 1 not found"))
                .verify();

        verify(playerRepository, times(1)).findById(playerId);
    }

    @Test
    void updatePlayerRanking_CreateNewPlayerIfNotFound() {
        String playerName = "Dolors";

        when(playerRepository.findByPlayerName(playerName)).thenReturn(Mono.empty());
        when(playerRepository.save(any(PlayerRanking.class))).thenReturn(Mono.just(new PlayerRanking("1", playerName, 1)));

        Mono<Void> result = playerService.updatePlayerRanking(playerName, true);

        StepVerifier.create(result).verifyComplete();

        verify(playerRepository, times(1)).findByPlayerName(playerName);
        verify(playerRepository, times(1)).save(any(PlayerRanking.class));
    }
}
