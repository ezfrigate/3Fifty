package com.three.fifty.models;

import com.three.fifty.enums.Team;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private int id;
    private int rank;
    private int pts;
    private Team team;
    private List<Card> currentHand;
    private List<List<TableCard>> wonHands;

    public void addInCurrentHand(Card card){
        if(currentHand == null){
            currentHand = new ArrayList<>();
        }
        currentHand.add(card);
    }
}
