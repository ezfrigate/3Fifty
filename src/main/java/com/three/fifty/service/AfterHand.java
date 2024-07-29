package com.three.fifty.service;

import com.three.fifty.enums.Suit;
import com.three.fifty.models.Card;
import com.three.fifty.models.GameState;
import com.three.fifty.models.TableCard;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AfterHand {
    public void calculatePlayersPts(){
        GameState gameState = GameState.getInstance();
        gameState.getPlayers().forEach(p-> p.setPts(
            p.getWonHands()
                .stream()
                .flatMap(Collection::stream)
                .mapToInt(Card::getPts)
                .sum()
        ));
    }

    public int calculateWinnerOfHand(){
        GameState gameState = GameState.getInstance();
        TableCard bestCard = gameState.getThisHand().get(0);
        Suit trumpSuit = gameState.isTrumpOn() ? gameState.getTrump() : null;
        for(int i =1; i<6; i++){
            System.out.println("Current Winner : " + bestCard.getSuit() + bestCard.getRank());
            if(bestCard.getSuit() == gameState.getThisHand().get(i).getSuit()){
                //If and only if both cards have same suit
                if(bestCard.getRank().getPointValue() < gameState.getThisHand().get(i).getRank().getPointValue()){
                    bestCard = gameState.getThisHand().get(i);
                }
            } else {
                //Code comes here if and only if both cards have different suit
                if(gameState.isTrumpOn()){
                    //Both cards are different and trump is on
                    if(bestCard.getSuit() == trumpSuit){
                        //Both cards are different, trump is on and best card is trump = BEST CARD REMAINS BEST CARD
                    } else if(gameState.getThisHand().get(i).getSuit() == trumpSuit){
                        //Both cards are different, trump is on and next card is trump = NEXT CARD BECOMES BEST CARD
                        bestCard = gameState.getThisHand().get(i);
                    }
                } else {
                    // Both cards are different and trump is off
                    // BEST CARD REMAINS THE BEST CARD
                }
            }
        }
        System.out.println("Current Winner : " + bestCard.getSuit() + bestCard.getRank());
        return bestCard.getPlayerId();
    }
}
