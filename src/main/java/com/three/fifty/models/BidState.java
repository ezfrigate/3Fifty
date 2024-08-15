package com.three.fifty.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BidState {
    private static BidState instance;

    private int id;
    private int currentPlayerId;
    private int firstPlayerId;
    private int callerPlayerId;
    private int currentBid;
    private List<Integer> droppedOutPlayers;
    private Card firstSupport;
    private Card secondSupport;
    private boolean bidFinish;

    // Static method to get the singleton instance of BidState
    public static BidState getInstance() {
        if (instance == null) {
            instance = new BidState();
        }
        return instance;
    }

    public void reset(){
        instance = new BidState();
    }

    private BidState(){
        // Perform initialization if necessary
        setId(1);
        setCurrentPlayerId(0);
        setFirstPlayerId(0);
        setCurrentBid(250);
        setDroppedOutPlayers(new ArrayList<>());
    }
}
