package com.three.fifty.service;

import com.three.fifty.exception.CardException;
import com.three.fifty.models.GameState;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class DuringHand {
    public void openTrump(){
        GameState gameState = GameState.getInstance();
        if(gameState.isTrumpOn()){
            throw new CardException(HttpStatus.NOT_ACCEPTABLE.value(), "Trump is already open.");
        } else {
            gameState.setTrumpOn(true);
        }
    }


}
