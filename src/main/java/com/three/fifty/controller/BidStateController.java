package com.three.fifty.controller;

import com.three.fifty.exception.CardException;
import com.three.fifty.exception.ErrorResponse;
import com.three.fifty.models.*;
import com.three.fifty.service.BeforeGame;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BidStateController {
    private final BeforeGame beforeGame;
    // Exception Handler method added in CustomerController to handle CustomerAlreadyExistsException
    @ExceptionHandler(value = CardException.class)
    public ResponseEntity<ErrorResponse> handle(CardException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getStatusCode(), ex.getMsg());
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode()); // Convert status code to HttpStatus
        return new ResponseEntity<>(errorResponse, status);
    }

    @GetMapping("/bidstate")
    public BidState getGameState(){
        return BidState.getInstance();
    }

    @PostMapping("/bid")
    public BidState bid(@RequestBody BidRequest bidRequest){
        if(BidState.getInstance().getCurrentPlayerId() != bidRequest.getPlayerId()){
            throw new CardException(400, "Not your turn.");
        } else {
            beforeGame.callBid(bidRequest.getPlayerId(), bidRequest.getBidValue());
        }
        return BidState.getInstance();
    }

    @PostMapping("/setSupport")
    public GameState setSupport(@RequestBody SupportCardSetRequest supportCardSetRequest){
        if(BidState.getInstance().getCallerPlayerId() != supportCardSetRequest.getPlayerId()){
            throw new CardException(400, "You are not the caller.");
        } else if(!BidState.getInstance().isBidFinish()){
            throw new CardException(400, "Wait for bid to finish.");
        } else {
            beforeGame.setSupportCards(supportCardSetRequest.getPlayerId(), supportCardSetRequest.getCards().get(0), supportCardSetRequest.getCards().get(1));
        } return GameState.getInstance();
    }
}
