package cat.itacademy.s05.t01.n01.controller;

import cat.itacademy.s05.t01.n01.model.PlayerRanking;
import cat.itacademy.s05.t01.n01.model.dto.playerDto.RankingDto;
import cat.itacademy.s05.t01.n01.model.dto.playerDto.UpdateNameDto;
import cat.itacademy.s05.t01.n01.service.impl.PlayerServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/player")
@Tag(name = "Player Management", description = "Endpoints to manage the players and the ranking, " +
        "related to the player service on MYSQL.")
public class PlayerController {
    @Autowired
    private PlayerServiceImpl playerServiceImpl;

    @GetMapping("/ranking")
    @Operation(
            summary = "Get players ranking",
            description = "Retrieves a list of the 10 players with more wins in descending order."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ranking list retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RankingDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    public Mono<ResponseEntity<List<RankingDto>>> getRanking() {
        return playerServiceImpl.getRanking()
                .collectList()
                .map(ResponseEntity::ok);
    }



    @PutMapping("/{playerId}")
    @Operation(
            summary = "Update player name",
            description = "Updates the name of an existing player based on the provided unique player ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "New name for the player.",
            required = true
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Player name updated successfully",
                    content = @Content(schema = @Schema(implementation = PlayerRanking.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "404", description = "Player not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Parameter(description = "ID of the player to update the name", required = true)
    public Mono<ResponseEntity<PlayerRanking>> updatePlayerName
            (@PathVariable String playerId, @RequestBody @Valid UpdateNameDto request){
        return playerServiceImpl.updatePlayerName(playerId, request).map(ResponseEntity::ok);
    }
}
