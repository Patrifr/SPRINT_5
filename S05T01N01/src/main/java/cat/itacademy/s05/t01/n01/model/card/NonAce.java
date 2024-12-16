package cat.itacademy.s05.t01.n01.model.card;

import cat.itacademy.s05.t01.n01.enums.Suit;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NonAce extends Card {
    private int value;

    public NonAce(String rank, Suit suit, int value){
        super(rank, suit);
        this.value = value;
    }
}
