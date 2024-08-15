package com.three.fifty.controller;

import com.three.fifty.exception.CardException;
import com.three.fifty.init.Deck;
import com.three.fifty.exception.ErrorResponse;
import com.three.fifty.models.GameState;
import com.three.fifty.models.PlayRequest;
import com.three.fifty.models.TableCard;
import com.three.fifty.service.AfterHand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class GameStateController {
    @Autowired
    private AfterHand afterHand;

    // Exception Handler method added in CustomerController to handle CustomerAlreadyExistsException
    @ExceptionHandler(value = CardException.class)
    public ResponseEntity<ErrorResponse> handle(CardException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getStatusCode(), ex.getMsg());
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode()); // Convert status code to HttpStatus
        return new ResponseEntity<>(errorResponse, status);
    }

    @GetMapping("/gamestate")
    public GameState getGameState(){
        return GameState.getInstance();
    }

    @PostMapping("/deal")
    public GameState dealCards(){
        GameState gameState = GameState.getInstance();
        Deck deck = Deck.getInstance();
        for(int i = 0; i < 48; i++){
            gameState.getPlayers().get(i%6).addInCurrentHand(deck.getCards().get(i));
        }
        return gameState;
    }


    @PostMapping("/play")
    public GameState play(@RequestBody PlayRequest playRequest){
        GameState gameState = GameState.getInstance();
        if(gameState.getCurrentPlayerId() != playRequest.getPlayerId()) {
            throw new CardException(400, "Not your turn");
        } else {
            gameState.getThisHand().add(
                    new TableCard(
                            gameState.getPlayers().get(gameState.getCurrentPlayerId()).getCurrentHand().get(playRequest.getCardNumber()),
                            playRequest.getPlayerId()
                    )
            );
            gameState.getPlayers().get(
                    gameState.getCurrentPlayerId()).getCurrentHand().remove(playRequest.getCardNumber()
            );
            gameState.setCurrentPlayerId((gameState.getCurrentPlayerId() + 1)%6);
            if(gameState.getThisHand().size() == 6){
                int playerId = afterHand.calculateWinnerOfHand();
                gameState.getPlayers().get(playerId).getWonHands().add(gameState.getThisHand());
                gameState.setThisHand(new ArrayList<>());
                gameState.setCurrentPlayerId(playerId);
            }
            return gameState;
        }
    }
}
