package com.three.fifty.models;

import com.three.fifty.enums.Rank;
import com.three.fifty.enums.Suit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    private Rank rank;
    private Suit suit;
    private int pts;
    public Card(Rank rank, Suit suit){
        this.rank = rank;
        this.suit = suit;
        this.pts = ptsFunction(rank,suit);
    }

    private int ptsFunction(Rank rank, Suit suit){
        if(Rank.ACE.equals(rank)) return 25;
        if(Rank.KING.equals(rank)) return 20;
        if(Rank.QUEEN.equals(rank)) return 15;
        if(Rank.JACK.equals(rank)) return 15;
        if(Rank.THREE.equals(rank) && Suit.SPADES.equals(suit)) return 50;
        return 0;
    }
}
