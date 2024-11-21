package cat.itacademy.s05.t01.n01.model.card;

import cat.itacademy.s05.t01.n01.enums.Suit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Ace implements Card {
    private String rank;
    private Suit suit;
    private int value;
}
