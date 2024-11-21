package cat.itacademy.s05.t01.n01.model.dto;

import jakarta.validation.constraints.NotEmpty;

public class RankingDto {
    private int id;
    @NotEmpty(message = "Player must have a  valid name.")
    private String name;
    private int score;
}
