package com.three.fifty.service;

import com.three.fifty.enums.Team;
import com.three.fifty.models.GameState;
import com.three.fifty.models.Player;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AfterGame {
    public void calculateWinners(){
        GameState gameState = GameState.getInstance();
        Map<Team, Integer> sums = gameState.getPlayers().stream()
                .collect(Collectors.groupingBy(
                        Player::getTeam,
                        Collectors.summingInt(Player::getPts)
                ));
    }
}
