package cat.itacademy.s05.t01.n01.model.dto;

import jakarta.validation.constraints.NotEmpty;

public class PlayerNameDto {
    @NotEmpty(message = "Player must have a  valid name.")
    private String name;
}
