package cat.itacademy.s05.t01.n01.model.player;

import cat.itacademy.s05.t01.n01.enums.CroupierStatus;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Croupier extends Player{
    private CroupierStatus croupierStatus;
}
