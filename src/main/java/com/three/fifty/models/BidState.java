package com.three.fifty.models;

import lombok.Data;

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

    private BidState(){
        // Perform initialization if necessary
        if (id == 0) {
            setId(1);
            setCurrentPlayerId(0);
            setFirstPlayerId(0);
            setCurrentBid(250);
        }
    }
}
