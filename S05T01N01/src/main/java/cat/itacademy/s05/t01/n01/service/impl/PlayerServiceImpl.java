package cat.itacademy.s05.t01.n01.service.impl;

import cat.itacademy.s05.t01.n01.exception.custom.DuplicatedNameException;
import cat.itacademy.s05.t01.n01.exception.custom.PlayerNotFoundException;
import cat.itacademy.s05.t01.n01.model.PlayerRanking;
import cat.itacademy.s05.t01.n01.model.dto.playerDto.RankingDto;
import cat.itacademy.s05.t01.n01.model.dto.playerDto.UpdateNameDto;
import cat.itacademy.s05.t01.n01.repository.PlayerRepository;
import cat.itacademy.s05.t01.n01.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Service
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    @Override
    public Flux<RankingDto> getRanking() {
        return playerRepository.findAll().
                sort((player1, player2) -> Integer.compare( player2.getWins(), player1.getWins()))
                .take(10).map(playerRanking -> new RankingDto(playerRanking.getPlayerName(), playerRanking.getWins()));
    }

    @Override
    public Mono<PlayerRanking> updatePlayerName(String id, UpdateNameDto request) {
        return playerRepository.findById(id)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Player with id: " + id + " not found")))
                .flatMap(existingPlayer -> playerRepository.findByPlayerName(request.getName())
                        .flatMap(playerWithNewName -> {
                            if (!playerWithNewName.getId().equals(id)) {
                                return Mono.error(new DuplicatedNameException("Name '" + request.getName() + "' is already taken."));
                            }
                            return Mono.just(existingPlayer);
                        })
                        .defaultIfEmpty(existingPlayer)
                        .flatMap(playerToUpdate -> {
                            existingPlayer.setPlayerName(request.getName());
                            return playerRepository.save(existingPlayer);
                        })
                );
    }

    public Mono<Void> updatePlayerRanking(String playerName, boolean hasWon) {
        return playerRepository.findByPlayerName(playerName).flatMap(existingPlayer -> {
                    if (hasWon) {
                        existingPlayer.setWins(existingPlayer.getWins() + 1);
                    }
                    return playerRepository.save(existingPlayer);
                }).switchIfEmpty(Mono.defer(() -> {
                    PlayerRanking newPlayer = new PlayerRanking();
                    newPlayer.setPlayerName(playerName);
                    newPlayer.setWins(hasWon ? 1 : 0);

                    return playerRepository.save(newPlayer);
                }))
                .then();
    }
}
