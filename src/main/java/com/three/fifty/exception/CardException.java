package com.three.fifty.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardException extends RuntimeException{
    private int statusCode;
    private String msg;
}
