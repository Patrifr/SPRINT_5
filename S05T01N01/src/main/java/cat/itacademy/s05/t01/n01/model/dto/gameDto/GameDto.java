package cat.itacademy.s05.t01.n01.model.dto.gameDto;

import cat.itacademy.s05.t01.n01.model.Game;
import cat.itacademy.s05.t01.n01.model.dto.playerDto.CroupierDto;
import cat.itacademy.s05.t01.n01.model.player.RegularPlayer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GameDto {
    private String gameId;
    private List<RegularPlayer> players;
    private CroupierDto croupier;

    public GameDto(Game game) {
        this.gameId = game.getId();
        this.players = game.getPlayers();
        this.croupier = new CroupierDto(game.getCroupier());
    }

}
