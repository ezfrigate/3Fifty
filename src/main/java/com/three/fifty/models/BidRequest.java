package com.three.fifty.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidRequest {
    private int playerId;
    private int bidValue;
}
