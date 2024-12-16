package cat.itacademy.s05.t01.n01.service;

import cat.itacademy.s05.t01.n01.model.PlayerRanking;
import cat.itacademy.s05.t01.n01.model.dto.playerDto.RankingDto;
import cat.itacademy.s05.t01.n01.model.dto.playerDto.UpdateNameDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PlayerService {
    Flux<RankingDto> getRanking();
    Mono<PlayerRanking> updatePlayerName(String id, UpdateNameDto request);
}
