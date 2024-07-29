package com.three.fifty.enums;

public enum Rank {
    ACE(13),
    KING(12),
    QUEEN(11),
    JACK(10),
    TEN(9),
    NINE(8),
    EIGHT(7),
    SEVEN(6),
    SIX(5),
    FIVE(4),
    FOUR(3),
    THREE(2);

    private final int pointValue;

    Rank(int pointValue) {
        this.pointValue = pointValue;
    }

    public int getPointValue() {
        return pointValue;
    }
}

