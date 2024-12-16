package cat.itacademy.s05.t01.n01.model.dto.playerDto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDto {
    @NotEmpty(message = "Player must have a  valid name.")
    private String name;
    private Integer bet;
}
