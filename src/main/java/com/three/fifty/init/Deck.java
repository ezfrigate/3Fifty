package com.three.fifty.init;

import com.three.fifty.enums.Rank;
import com.three.fifty.enums.Suit;
import com.three.fifty.models.Card;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class Deck {
    private static Deck instance; // Hold the single instance
    private List<Card> cards;

    public static Deck getInstance() {
        if (instance == null) { // Check if instance exists
            instance = new Deck(); // Create instance if not present
        }
        Collections.shuffle(instance.cards);
        return instance;
    }

    public Deck() {
        this.cards = Stream.of(Rank.values())
                .flatMap(rank -> Stream.of(Suit.values())
                        .map(suit -> new Card(rank, suit, ptsFunction(rank, suit))))
                .collect(Collectors.toList());
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
