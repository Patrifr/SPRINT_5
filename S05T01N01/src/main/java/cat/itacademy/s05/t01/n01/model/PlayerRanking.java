package cat.itacademy.s05.t01.n01.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "ranking")
public class PlayerRanking {
    @Id private String id;
    @Column private String playerName;
    @Column private int wins;
}
