package cat.itacademy.s05.t01.n01.model.player;

import cat.itacademy.s05.t01.n01.enums.PlayerFinalStatus;
import cat.itacademy.s05.t01.n01.model.card.Card;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Document
public abstract class Player {
    @Setter private PlayerFinalStatus playerFinalStatus;
    private int handValue;
    private List<Card> hand = new ArrayList<>();

    public void addCard(Card card){
        this.hand.add(card);
    }
}
