package cat.itacademy.s05.t01.n01.model.player;

import cat.itacademy.s05.t01.n01.enums.PlayerStatus;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;


@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"name", "playerStatus", "bet", "handValue", "hand"})
public class RegularPlayer extends Player {

    @NotEmpty(message = "Player must have a  valid name.")
    @Setter private String name;

    @Setter private Integer bet;
    @Setter private PlayerStatus playerStatus;


}
