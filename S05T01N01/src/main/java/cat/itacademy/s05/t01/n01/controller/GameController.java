package cat.itacademy.s05.t01.n01.controller;

import cat.itacademy.s05.t01.n01.model.Game;
import cat.itacademy.s05.t01.n01.model.dto.gameDto.GameResponse;
import cat.itacademy.s05.t01.n01.model.dto.gameDto.PlayRequest;
import cat.itacademy.s05.t01.n01.model.dto.gameDto.NewGameDto;
import cat.itacademy.s05.t01.n01.model.dto.playerDto.PlayerDto;
import cat.itacademy.s05.t01.n01.service.impl.GameServiceImpl;
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


import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/game")
@Tag(name = "Game Management", description = "Endpoints to manage the game, related to the Game Service on MongoDB.")
public class GameController {

    @Autowired
    private GameServiceImpl gameServiceImpl;


    @PostMapping("/new")
    @Operation(
            summary = "Create a new Game.",
            description = "Creates a new game with the provided list of players. Returns the game details of the new Game.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Names and initial bets for the players",
                    required = true
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Game created successfully",
                    content = @Content(schema = @Schema(implementation = NewGameDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<ResponseEntity<NewGameDto>> createGame(@RequestBody @Valid List<PlayerDto> players){
        System.out.println("Players received: " + players);
        return gameServiceImpl.createGame(players).map(game -> {
            NewGameDto gameDto = new NewGameDto(game);

            URI location = URI.create("/game/" + game.getId());
            return ResponseEntity.created(location).body(gameDto);
        }).onErrorResume(e -> Mono.just(ResponseEntity.badRequest().build()));
    }


    @GetMapping("/{id}")
    @Operation(
            summary = "Get game details.",
            description = "Retrieves the details of a game based on its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Game details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = Game.class))),
            @ApiResponse(responseCode = "404", description = "Game not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Parameter(description = "Unique ID of the game.", required = true)
    public Mono<ResponseEntity<Game>> getGameDetails(@PathVariable String id){
        return gameServiceImpl.getGameDetails(id).map(ResponseEntity::ok);
    }


    @PostMapping("/{id}/play")
    @Operation(
            summary = "Player makes a move in the game.",
            description = "Allows players to make a move and to increase the bet in the game identified by its unique game ID.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Current player's move and incremental bet.",
                    required = false
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Move processed successfully",
                    content = @Content(schema = @Schema(implementation = GameResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid move or input data"),
            @ApiResponse(responseCode = "404", description = "Game not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Parameter(description = "Unique ID of the game.", required = true)
    public Mono<ResponseEntity<GameResponse>> makeAMove(@PathVariable String id, @RequestBody(required = false) PlayRequest playRequest) {
        return gameServiceImpl.play(id, playRequest)
                .map(ResponseEntity::ok);
    }


    @DeleteMapping("/{id}/delete")
    @Operation(
            summary = "Delete a game",
            description = "Deletes a game identified by its unique ID from the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Game deleted successfully, no content returned"),
            @ApiResponse(responseCode = "404", description = "Game not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Parameter(description = "Unique ID of the game to be deleted", required = true)
    public Mono<ResponseEntity<Game>> deleteGame(@PathVariable String id){
        return gameServiceImpl.deleteGame(id).then(Mono.just(ResponseEntity.noContent().build()));
    }
}
