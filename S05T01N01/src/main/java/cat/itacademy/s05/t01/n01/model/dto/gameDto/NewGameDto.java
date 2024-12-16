package cat.itacademy.s05.t01.n01.model.dto.gameDto;

import cat.itacademy.s05.t01.n01.model.Game;
import cat.itacademy.s05.t01.n01.model.dto.playerDto.CroupierDto;
import cat.itacademy.s05.t01.n01.model.player.RegularPlayer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewGameDto {
    private String gameId;
    private String nextTurn;
    private List<RegularPlayer> players;
    private CroupierDto croupier;

    public NewGameDto(Game game) {
        this.gameId = game.getId();
        this.nextTurn = game.getPlayers().getFirst().getName();
        this.players = game.getPlayers();
        this.croupier = new CroupierDto(game.getCroupier());
    }

}
