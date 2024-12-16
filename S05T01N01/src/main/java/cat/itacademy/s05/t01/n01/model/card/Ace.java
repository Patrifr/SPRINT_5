package cat.itacademy.s05.t01.n01.model.card;

import cat.itacademy.s05.t01.n01.enums.Suit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Ace extends Card {

    public Ace (Suit suit){
        super("Ace", suit);
    }
}
