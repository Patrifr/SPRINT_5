package cat.itacademy.s05.t01.n01.service;

import cat.itacademy.s05.t01.n01.model.Game;
import cat.itacademy.s05.t01.n01.model.dto.gameDto.GameResponse;
import cat.itacademy.s05.t01.n01.model.dto.gameDto.PlayRequest;
import cat.itacademy.s05.t01.n01.model.dto.playerDto.PlayerDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GameService {
    //Crear partida, Obtenir detalls de partida, Realitzar jugada, Eliminar partida,
    Mono<Game> createGame(List<PlayerDto> players);
    Mono<Game> getGameDetails(String id);
    Mono<Void> deleteGame(String id);
    Mono<GameResponse> play(String id, PlayRequest action);
}
