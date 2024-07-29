package com.three.fifty.service;

import com.three.fifty.enums.Team;
import com.three.fifty.exception.CardException;
import com.three.fifty.models.BidState;
import com.three.fifty.models.Card;
import com.three.fifty.models.GameState;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class BeforeGame {
    public void callBid(int playerId, int bid){
        BidState bidState = BidState.getInstance();
        if(!bidState.getDroppedOutPlayers().contains(playerId)) {
            if (playerId == bidState.getCurrentPlayerId()) {
                if(bid > bidState.getCurrentBid()) {
                    bidState.setCurrentBid(bid);
                    bidState.setCallerPlayerId(playerId);
                } else if (bid == 0) {
                    bidState.getDroppedOutPlayers().add(playerId);
                } else {
                    throw new CardException(HttpStatus.NOT_ACCEPTABLE.value(), "Bid value has to be greater than the last bid.");
                }
            } else {
                throw new CardException(HttpStatus.NOT_ACCEPTABLE.value(), "Not your turn to Bid.");
            }
        } else {
            throw new CardException(HttpStatus.NOT_ACCEPTABLE.value(), "You passed your previous opportunity.");
        }
    }

    private boolean checkBidFinish(){
        BidState bidState = BidState.getInstance();
        return bidState.getDroppedOutPlayers().size() == 5;
    }

    public void setSupportCards(Card firstCard, Card secondCard){
        BidState bidState = BidState.getInstance();
        bidState.setFirstSupport(firstCard);
        bidState.setSecondSupport(secondCard);

        GameState gameState = GameState.getInstance();
        gameState.getPlayers().forEach(player -> {
            if(player.getCurrentHand().contains(firstCard) || player.getCurrentHand().contains(secondCard)){
                player.setTeam(Team.DEFENDERS);
            } else {
                player.setTeam(Team.ALLIES);
            }
        });
    }
}
