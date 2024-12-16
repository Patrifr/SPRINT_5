package cat.itacademy.s05.t01.n01.model.dto.playerDto;

import cat.itacademy.s05.t01.n01.enums.PlayerFinalStatus;
import cat.itacademy.s05.t01.n01.model.card.Card;
import cat.itacademy.s05.t01.n01.model.player.Croupier;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CroupierDto {
   private int handvalue;
    private List<Card> visibleHand;
    private PlayerFinalStatus playerFinalStatus;


    public CroupierDto(Croupier croupier){
       this.handvalue = croupier.getHandValue();
       this.visibleHand = croupier.getHand().stream().filter(Card::isFaceUp).toList();
       this.playerFinalStatus = croupier.getPlayerFinalStatus();
   }

}
