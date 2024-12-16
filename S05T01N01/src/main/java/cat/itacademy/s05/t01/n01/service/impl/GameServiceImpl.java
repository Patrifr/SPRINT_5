package cat.itacademy.s05.t01.n01.service.impl;

import cat.itacademy.s05.t01.n01.enums.*;
import cat.itacademy.s05.t01.n01.exception.custom.DuplicatedNameException;
import cat.itacademy.s05.t01.n01.exception.custom.GameNotFoundException;
import cat.itacademy.s05.t01.n01.model.Game;
import cat.itacademy.s05.t01.n01.model.card.Ace;
import cat.itacademy.s05.t01.n01.model.card.Card;
import cat.itacademy.s05.t01.n01.model.card.NonAce;
import cat.itacademy.s05.t01.n01.model.dto.gameDto.GameDto;
import cat.itacademy.s05.t01.n01.model.dto.gameDto.GameResponse;
import cat.itacademy.s05.t01.n01.model.dto.gameDto.PlayRequest;
import cat.itacademy.s05.t01.n01.model.dto.playerDto.PlayerDto;
import cat.itacademy.s05.t01.n01.model.player.Croupier;
import cat.itacademy.s05.t01.n01.model.player.RegularPlayer;
import cat.itacademy.s05.t01.n01.repository.GameRepository;
import cat.itacademy.s05.t01.n01.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerServiceImpl playerService;

    @Override
    public Mono<Game> createGame(List<PlayerDto> players) {
        Set<String> uniqueNames = new HashSet<>();
        Set<String> duplicateNames = new HashSet<>();

        players.forEach(player -> {
            if (!uniqueNames.add(player.getName())) {
                duplicateNames.add(player.getName());
            }
        });

        if (!duplicateNames.isEmpty()) {
            return Mono.error(new DuplicatedNameException("Duplicated player names: " + String.join(", ", duplicateNames)));
        }

        return Flux.fromIterable(players)
                .flatMap(player -> playerService.updatePlayerRanking(player.getName(), false))
                .then(Mono.defer(() -> {
                    List<RegularPlayer> gamePlayers = mapGamePLayerDto(players);
                    Game game = initializeGame(gamePlayers);
                    game.setGameStatus(GameStatus.CREATED);
                    return gameRepository.save(game);
                }));
    }

    @Override
    public Mono<Game> getGameDetails(String id) {
        return findGameById(id);
    }

    @Override
    public Mono<Void> deleteGame(String id) {
        return findGameById(id).flatMap(existingGame -> gameRepository.deleteById(id));
    }

    @Override
    public Mono<GameResponse> play(String id, PlayRequest playRequest) {
        return findGameById(id).flatMap(game -> {

            if(game.getGameStatus() == GameStatus.CREATED){
                game.setGameStatus(GameStatus.ONGOING);
            }

            if (game.getCroupier().getCroupierStatus() == CroupierStatus.PLAYING_TURN) {
                return Mono.just(new GameResponse(new GameDto(game), "It's the croupier's turn."));
            }

            if (game.getCroupier().getCroupierStatus() == CroupierStatus.FINISHED_TURN
                    && playRequest.getAction() == Action.END) {
                return finishCroupierTurn(game);
            }

            RegularPlayer currentPlayer = getCurrentPlayer(game);

            if(currentPlayer.getPlayerStatus() == PlayerStatus.BLACKJACK){
                return generateNextTurnMessage(game, currentPlayer, "has Blackjack!");
            }

            if(playRequest.getAction() == Action.HIT){
                if(playRequest.getHigherBet() != null && playRequest.getHigherBet() > 0){
                    currentPlayer.setBet(currentPlayer.getBet() + playRequest.getHigherBet());                 }
                return gameRepository.save(game).then(actionIsHit(game, currentPlayer));

            } else if (playRequest.getAction() == Action.STAND) {
                    return actionIsStand(game, currentPlayer);
            }

            return Mono.error(new IllegalArgumentException("Invalid Action"));
        });
    }

    //todo game:

    private Mono<GameResponse> finishCroupierTurn(Game game) {
        if (game.getCroupier().getCroupierStatus() == CroupierStatus.FINISHED_TURN) {
            return finalizeGame(game);
        }

        return Mono.just(new GameResponse(new GameDto(game), "Croupier is still playing."));
    }

    private Mono<GameResponse> finalizeGame(Game game) {
        Croupier croupier = game.getCroupier();

        if (croupier.getHandValue() > 21) {
            croupier.setPlayerFinalStatus(PlayerFinalStatus.LOSE);
        }

        if (croupier.getPlayerFinalStatus() == PlayerFinalStatus.LOSE) {
            game.getPlayers().forEach(player -> {
                if (player.getPlayerStatus() != PlayerStatus.BUST) {
                    player.setPlayerFinalStatus(PlayerFinalStatus.WIN);
                } else {
                    player.setPlayerFinalStatus(PlayerFinalStatus.LOSE);
                }
            });

        } else {
            game.getPlayers().forEach(player -> {
                if (player.getPlayerStatus() == PlayerStatus.BUST) {
                    player.setPlayerFinalStatus(PlayerFinalStatus.LOSE);
                } else if (player.getHandValue() > croupier.getHandValue()) {
                    player.setPlayerFinalStatus(PlayerFinalStatus.WIN);
                } else if (player.getHandValue() < croupier.getHandValue()) {
                    player.setPlayerFinalStatus(PlayerFinalStatus.LOSE);
                } else {
                    player.setPlayerFinalStatus(PlayerFinalStatus.PUSH);
                    croupier.setPlayerFinalStatus(PlayerFinalStatus.PUSH);
                }
            });

            if (game.getPlayers().stream().noneMatch(player -> player.getPlayerFinalStatus() == PlayerFinalStatus.WIN)) {
                croupier.setPlayerFinalStatus(PlayerFinalStatus.WIN);
            } else if (croupier.getPlayerFinalStatus() == null) {
                // Asegurar que el croupier no quede con estado null
                croupier.setPlayerFinalStatus(PlayerFinalStatus.LOSE);
            }
        }

        game.setGameStatus(GameStatus.FINISHED);

        // para construir el mensaje final
        StringBuilder finalMessage = new StringBuilder("Game is over. Results: ");
        game.getPlayers().forEach(player ->
                finalMessage.append(player.getName())
                        .append(": ")
                        .append(player.getPlayerFinalStatus())
                        .append(", ")
        );
        finalMessage.append("Croupier: ").append(croupier.getPlayerFinalStatus()).append(".");

        // Guardar cambios y retornar respuesta final
        return Flux.fromIterable(game.getPlayers())
                .flatMap(player -> playerService.updatePlayerRanking(
                        player.getName(),
                        player.getPlayerFinalStatus() == PlayerFinalStatus.WIN
                ))
                .then(gameRepository.save(game))
                .map(updatedGame -> new GameResponse(
                        new GameDto(updatedGame),
                        finalMessage.toString()
                ));
    }

   private Game initializeGame(List<RegularPlayer> regularPlayers){
        Game newGame = new Game();
        Croupier croupier = new Croupier();
        newGame.setPlayers(regularPlayers);
        newGame.setCroupier(croupier);
        croupier.setCroupierStatus(CroupierStatus.WAITING_TURN);
        shuffleDeck(newGame);
        dealInitialCards(newGame);
        allPlayersHandValue(newGame);
        calculateCroupierHandValue(newGame);
        return newGame;
    }

    private Mono<Game> findGameById(String id) {
        return gameRepository.findById(id)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Error. Game with id " + id + "not found.")));
    }


    //todo MÈTODOS MOVIMIENTOS CROUPIER

    private Mono<Game> processCroupierMoves(Game game){
        Croupier croupier = game.getCroupier();

        while(croupier.getHandValue() < 17){
            croupier.addCard(drawCard(game));
            calculateCroupierHandValue(game);
        }
        croupier.getHand().forEach(card -> card.setFaceUp(true));

        croupier.setCroupierStatus(CroupierStatus.FINISHED_TURN);
        return gameRepository.save(game);
    }

    //todo MÉTODOS MOVIMIENTOS JUGADORES

    private Mono<GameResponse> actionIsHit(Game game, RegularPlayer currentPlayer){
        return processHit(game).flatMap(updatedGame -> {
            //Si el player hace bust se pasO al siguiente turnoo
            if (currentPlayer.getPlayerStatus() == PlayerStatus.BUST) {
                return generateNextTurnMessage(updatedGame, currentPlayer, "busted. Your final hand value: " + currentPlayer.getHandValue());
            }
            return Mono.just(new GameResponse(new GameDto(updatedGame), currentPlayer.getName() + " hit and drew a card. It's your turn again!" +
                    "Your actual hand value: " + currentPlayer.getHandValue()));
        });
    }

    private Mono<GameResponse> actionIsStand(Game game, RegularPlayer currentPlayer){
        return processStand(game).flatMap(updatedGame ->
        generateNextTurnMessage(updatedGame, currentPlayer, "stands." +
                "Your final hand value: " + currentPlayer.getHandValue()));
    }

    private Mono<Game> processHit(Game game) {
        return Mono.defer(() -> {
            RegularPlayer currentPlayer = getCurrentPlayer(game);

            if (currentPlayer.getPlayerStatus() == PlayerStatus.PLAYING) {
                currentPlayer.addCard(drawCard(game));
                playerSetHandValue(currentPlayer);

                if (checkIfBust(currentPlayer)) {
                    if (game.getPlayerTurn() + 1 < game.getPlayers().size()) {
                        game.setPlayerTurn(game.getPlayerTurn() + 1);
                    }
                }

                return gameRepository.save(game).then(Mono.just(game));
            }

            return nextTurn(game);
        });
    }

   private Mono<Game> processStand(Game game) {
       return Mono.defer(() -> {
           RegularPlayer currentPlayer = getCurrentPlayer(game);

           playerSetHandValue(currentPlayer);
           currentPlayer.setPlayerStatus(PlayerStatus.STANDING);

           if (game.getPlayerTurn() + 1 < game.getPlayers().size()) {
               game.setPlayerTurn(game.getPlayerTurn() + 1);
           } else {
               game.getCroupier().setCroupierStatus(CroupierStatus.PLAYING_TURN);
               return processCroupierMoves(game);
           }

           return gameRepository.save(game).flatMap(this::nextTurn);
       });
   }

    private boolean checkIfBust(RegularPlayer currentPlayer){
        if(currentPlayer.getHandValue() > 21){
            currentPlayer.setPlayerStatus(PlayerStatus.BUST);
            return true;
        }
        return false;
    }

    //todo turnos
    private Mono<GameResponse> generateNextTurnMessage(Game game, RegularPlayer currentPlayer, String actionMessage) {
        List<RegularPlayer> players = game.getPlayers();
        String nextPlayerName;
        String message;
        int nextPlayerHandValue = 0;
        int currentIndex = players.indexOf(currentPlayer);

        if (currentIndex == players.size() - 1) {
            nextPlayerName = "Croupier";
            message = currentPlayer.getName() + " " + actionMessage + " It's Croupier's turn now!";
        } else {
            nextPlayerName = players.get(currentIndex + 1).getName();
            nextPlayerHandValue = players.get(currentIndex + 1).getHandValue();
            message = currentPlayer.getName() + " " + actionMessage + ". Next turn: " + nextPlayerName + ", "
                    + nextPlayerName + " your hand value is " + nextPlayerHandValue + ".";
        }

        return gameRepository.save(game)
                .thenReturn(new GameResponse(new GameDto(game), message));
    }

    private Mono<Game> nextTurn(Game game) {
        return Mono.defer(() -> {
            int currentPlayerTurn = game.getPlayerTurn();
            List<RegularPlayer> players = game.getPlayers();

            boolean allPlayersDone = players.stream().allMatch(player ->
                    player.getPlayerStatus() == PlayerStatus.STANDING ||
                            player.getPlayerStatus() == PlayerStatus.BUST ||
                            player.getPlayerStatus() == PlayerStatus.BLACKJACK);

            if (allPlayersDone) {
                game.getCroupier().setCroupierStatus(CroupierStatus.PLAYING_TURN);
                return processCroupierMoves(game)
                        .flatMap(updatedGame -> gameRepository.save(updatedGame));
            }

            if (currentPlayerTurn < players.size()) {
                RegularPlayer currentPlayer = players.get(currentPlayerTurn);

                if (currentPlayer.getPlayerStatus() == PlayerStatus.STANDING ||
                        currentPlayer.getPlayerStatus() == PlayerStatus.BUST ||
                        currentPlayer.getPlayerStatus() == PlayerStatus.BLACKJACK) {

                    game.setPlayerTurn(currentPlayerTurn + 1);
                    return gameRepository.save(game);
                }
            }

            return Mono.just(game);
        });
    }

    //todo metodos auxiliares players:
    private RegularPlayer getCurrentPlayer(Game game){
        return game.getPlayers().get(game.getPlayerTurn());
    }

    //mapear jugador
    private List<RegularPlayer> mapGamePLayerDto(List<PlayerDto> playersDto){ //Recibe la lista de jugadores simples
        return playersDto.stream().map(playerDto -> RegularPlayer.builder()
                .name(playerDto.getName()).bet(playerDto.getBet()).playerStatus(PlayerStatus.PLAYING).build()).toList();
    }

    //todo handvalues:
    //Metodo para saber en hand value del coupier
    public void calculateCroupierHandValue(Game game){
        Croupier croupier = game.getCroupier();
        List<Card> visibleCards = croupier.getHand().stream()
                .filter(Card::isFaceUp)
                .toList();

        int handValue = calculateHandValue(visibleCards);

        croupier.setHandValue(handValue);
    }

    //Método para saber el hand value de un jugador
    public int calculateHandValue(List<Card> hand){
        int totalValue = 0;
        int asCount = 0;

        for(Card card : hand){
            if(card instanceof NonAce){
                totalValue += ((NonAce) card).getValue();
            } else if (card instanceof Ace) {
                asCount++;
                totalValue += 11;
            }
        }
        while(totalValue > 21 && asCount > 0){
            totalValue -= 10;
            asCount--;
        }

        return totalValue;
    }

    public void playerSetHandValue(RegularPlayer player){
        player.setHandValue(calculateHandValue(player.getHand()));
    }

    //Metodo saber hand value de cada jugador
    public void allPlayersHandValue(Game game){
        game.getPlayers().forEach(player->{
            player.setHandValue(calculateHandValue(player.getHand()));
        });
    }

    //todo cartas:
    //Metodo para repartir las cartas a los jugadores y al cupier
    public  Mono<Game> dealInitialCards(Game game){
        //Repartir las cartas a los jugadores
        game.getPlayers().forEach(player-> {
            player.getHand().add(drawCard(game));
            player.getHand().add(drawCard(game));
            playerSetHandValue(player);
            if (player.getHandValue() == 21) {
                player.setPlayerStatus(PlayerStatus.BLACKJACK);
            }
        });

        //Repartir cartas al croupier y ocultar una
        Croupier croupier = game.getCroupier();
        Card hiddenCard = drawCard(game);
        hiddenCard.setFaceUp(false);

        croupier.getHand().add(drawCard(game));
        croupier.getHand().add(hiddenCard);

        return Mono.just(game);
    }

    //Metodo para coger carta
    private Card drawCard(Game game){
        List<Card> deck = game.getDeck();
        if(deck.isEmpty()){
            deck.addAll(createDeck());
            shuffleDeck(game);
        }
        return deck.removeFirst();
    }

    //Metodo para barajar
    private void shuffleDeck(Game game){
        game.setDeck(createDeck());
        Collections.shuffle(game.getDeck());
    }

    //Crear mazo
    private List<Card> createDeck(){
        List<Card> newDeck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (int i = 2; i <= 10; i++) {
                newDeck.add(new NonAce(String.valueOf(i), suit, i));
            }
            newDeck.add(new NonAce("J", suit, 10));
            newDeck.add(new NonAce("Q", suit, 10));
            newDeck.add(new NonAce("K", suit, 10));

            newDeck.add(new Ace(suit));
        }
        return newDeck;
    }
}
