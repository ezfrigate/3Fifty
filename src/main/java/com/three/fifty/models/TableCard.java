package com.three.fifty.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TableCard extends Card{
    private int playerId;
    public TableCard(Card card, int playerId){
        super(card.getRank(), card.getSuit(), card.getPts());
        this.playerId = playerId;
    }
}
