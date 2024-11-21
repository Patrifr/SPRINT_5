package cat.itacademy.s05.t01.n01.model.player;

import cat.itacademy.s05.t01.n01.enums.PlayerStatus;
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
public abstract class Player {
    @Id private String id;
    @Setter private int score;
    @Setter private List<Card> hand;
    @Setter private PlayerStatus playerStatus;
    @Setter private boolean playing = true;
}
