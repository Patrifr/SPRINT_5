package cat.itacademy.s05.t01.n01.model;

import cat.itacademy.s05.t01.n01.enums.GameStatus;
import cat.itacademy.s05.t01.n01.model.card.Card;
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
    @Setter private List<Card> players;
    @Setter private GameStatus gameStatus;
    @Setter private List<Card> deck;
    @Setter private int playerTurn;
}
