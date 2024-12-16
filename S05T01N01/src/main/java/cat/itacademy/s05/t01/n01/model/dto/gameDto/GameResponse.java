package cat.itacademy.s05.t01.n01.model.dto.gameDto;

import cat.itacademy.s05.t01.n01.model.Game;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GameResponse {
    private String rules;
    private String message;
    private GameDto gameDto;


    public GameResponse (GameDto gameDto, String message){
        this.rules = "Please when the last player stands, write 'END' as action to see the final results.";
        this.message = message;
        this.gameDto = gameDto;
    }
}
