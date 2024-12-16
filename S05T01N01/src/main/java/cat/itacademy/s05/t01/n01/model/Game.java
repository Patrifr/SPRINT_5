package cat.itacademy.s05.t01.n01.model;

import cat.itacademy.s05.t01.n01.enums.GameStatus;
import cat.itacademy.s05.t01.n01.model.card.Card;
import cat.itacademy.s05.t01.n01.model.player.Croupier;
import cat.itacademy.s05.t01.n01.model.player.RegularPlayer;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Document
public class Game {
    @Id private String id;
    @Setter private GameStatus gameStatus;
    @Setter private List<RegularPlayer> players;
    @Setter private Croupier croupier;
    @Setter private List<Card> deck;
    @Setter private int playerTurn;
}
