package com.three.fifty.models;

import com.three.fifty.enums.Suit;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static com.three.fifty.enums.Team.UNDEFINED;

@Data
public class GameState {
    private static GameState instance;

    private int id;
    private int currentPlayerId;
    private Card firstCard;
    private String firstPlayerId;
    private boolean trumpOn;
    private Suit trump;
    private List<TableCard> thisHand;
    private List<Player> players;

    // Static method to get the singleton instance of GameState
    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    // Method to create the initial game state
    private GameState() {
        // Perform initialization if necessary
        if (id == 0) {
            setId(1);
            setPlayers(createPlayers()); // You need to implement createPlayers() method
            setTrumpOn(false);
            setTrump(null);
            setCurrentPlayerId(0);
            setThisHand(new ArrayList<>());
        }
    }

    private List<Player> createPlayers() {
        List<Player> players = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            players.add(new Player(i, 0, 0, UNDEFINED, new ArrayList<>(), new ArrayList<>()));
        }
        return players;
    }

}
