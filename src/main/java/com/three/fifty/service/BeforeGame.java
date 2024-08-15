package com.three.fifty.service;

import com.three.fifty.enums.Team;
import com.three.fifty.exception.CardException;
import com.three.fifty.models.BidState;
import com.three.fifty.models.Card;
import com.three.fifty.models.GameState;
import com.three.fifty.models.Player;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BeforeGame {
    public void callBid(int playerId, int bid){
        BidState bidState = BidState.getInstance();
        if(!bidState.isBidFinish()) {
            if (!bidState.getDroppedOutPlayers().contains(playerId)) {
                if (playerId == bidState.getCurrentPlayerId()) {
                    if (bid == 350) {
                        bidState.setCurrentBid(bid);
                        bidState.setCallerPlayerId(playerId);
                        bidState.setBidFinish(true);
                    } else if (bid > bidState.getCurrentBid()) {
                        bidState.setCurrentBid(bid);
                        bidState.setCallerPlayerId(playerId);
                        bidState.setCurrentPlayerId(this.calculateNextPlayerId(playerId));
                    } else if (bid == 0) {
                        bidState.getDroppedOutPlayers().add(playerId);
                        if (bidState.getDroppedOutPlayers().size() == 5) {
                            bidState.setBidFinish(true);
                        }
                        bidState.setCurrentPlayerId(this.calculateNextPlayerId(playerId));
                    } else {
                        throw new CardException(HttpStatus.NOT_ACCEPTABLE.value(), "Bid value has to be greater than the last bid.");
                    }
                } else {
                    throw new CardException(HttpStatus.NOT_ACCEPTABLE.value(), "Not your turn to Bid.");
                }
            } else {
                throw new CardException(HttpStatus.NOT_ACCEPTABLE.value(), "You passed your previous opportunity.");
            }
        } else {
            throw new CardException(HttpStatus.NOT_ACCEPTABLE.value(), "Bidding is finished already.");
        }
    }

    private int calculateNextPlayerId(int playerId){
        BidState bidState = BidState.getInstance();
        for(int i =0; i < 6; i++){
            int nextPlayerId = (++playerId)%6;
            if(!bidState.getDroppedOutPlayers().contains(nextPlayerId)) return nextPlayerId;
        }
        return -1;
    }

    public void setSupportCards(int playerId, Card firstCard, Card secondCard){
        GameState gameState = GameState.getInstance();
        Player player = gameState.getPlayers().get(playerId);
        Optional<Card> possibleFirstCard = player.getCurrentHand().stream().filter(card -> card.getRank().equals(firstCard.getRank()) && card.getSuit().equals(firstCard.getSuit())).findAny();
        if(possibleFirstCard.isPresent()){
            throw new CardException(400, "Cannot request your own card.");
        }
        Optional<Card> possibleSecondCard = player.getCurrentHand().stream().filter(card -> card.getRank().equals(secondCard.getRank()) && card.getSuit().equals(secondCard.getSuit())).findAny();
        if(possibleSecondCard.isPresent()){
            throw new CardException(400, "Cannot request your own card.");
        }

        BidState bidState = BidState.getInstance();
        bidState.setFirstSupport(firstCard);
        bidState.setSecondSupport(secondCard);

        gameState.getPlayers().forEach(p -> {
            if(p.getCurrentHand().stream().anyMatch(card -> card.getRank().equals(secondCard.getRank()) && card.getSuit().equals(secondCard.getSuit())) || p.getCurrentHand().stream().anyMatch(card -> card.getRank().equals(firstCard.getRank()) && card.getSuit().equals(firstCard.getSuit()))){
                p.setTeam(Team.DEFENDERS);
            } else {
                p.setTeam(Team.ALLIES);
            }
        });
        gameState.getPlayers().get(playerId).setTeam(Team.DEFENDERS);
    }


}
