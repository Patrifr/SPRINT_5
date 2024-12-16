package cat.itacademy.s05.t01.n01.repository;

import cat.itacademy.s05.t01.n01.model.PlayerRanking;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PlayerRepository extends R2dbcRepository<PlayerRanking, String> {
    Mono<PlayerRanking> findByPlayerName(String playerName);
}
