package com.three.fifty.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SupportCardSetRequest {
    private int playerId;
    private List<Card> cards;
}
