package cat.itacademy.s05.t01.n01.model.dto.gameDto;

import cat.itacademy.s05.t01.n01.enums.Action;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayRequest {
    private Action action;
    private Integer higherBet;
}
