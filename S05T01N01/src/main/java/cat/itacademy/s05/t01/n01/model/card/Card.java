package cat.itacademy.s05.t01.n01.model.card;


import cat.itacademy.s05.t01.n01.enums.Suit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Card {
    private String rank;
    private Suit suit;
    private boolean isFaceUp;

    public Card (String rank, Suit suit){
        this.rank = rank;
        this.suit = suit;
        this.isFaceUp = true;
    }
}
