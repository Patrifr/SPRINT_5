package cat.itacademy.s05.t01.n01.repository;

import cat.itacademy.s05.t01.n01.model.Ranking;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface RankingRepository extends R2dbcRepository<Ranking, Integer> {
}
