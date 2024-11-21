package cat.itacademy.s05.t01.n01.model.player;

import jakarta.validation.constraints.NotEmpty;

public class RegularPlayer extends Player {
    @NotEmpty(message = "Player must have a  valid name.")
    private String name;
    private double bet;
}
