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
}
