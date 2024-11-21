package cat.itacademy.s05.t01.n01.repository;

import cat.itacademy.s05.t01.n01.model.player.Player;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PlayerRepository extends ReactiveMongoRepository<Player, String>{
}
