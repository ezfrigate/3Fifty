package com.three.fifty.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.three.fifty.controller.BidStateController;
import com.three.fifty.enums.Rank;
import com.three.fifty.enums.Suit;
import com.three.fifty.enums.Team;
import com.three.fifty.models.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(BidStateController.class)
public class BidControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        // Reset BidState before each test
        BidState bidState = BidState.getInstance();
        bidState.reset(); // Ensure you have a method to reset the state if needed
    }

    @Test
    public void testBiddingProcess() throws Exception {
        // Player 0 bids 300
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(0, 300)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Player 1 passes
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(1, 0)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Check the final state of the bid
        mockMvc.perform(get("/bidstate")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.currentBid").value(300))
                .andExpect(MockMvcResultMatchers.jsonPath("$.currentPlayerId").value(2))
                .andExpect(MockMvcResultMatchers.jsonPath("$.droppedOutPlayers[0]").value(1))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testInvalidBidLessThanCurrentBid() throws Exception {
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(0, 300)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Player 1 attempts to bid less than the current bid
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(1, 250)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("Bid value has to be greater than the last bid."))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testPlayerNotTurn() throws Exception {
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(0, 300)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Player 2 attempts to bid out of turn
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(2, 350)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("Not your turn."))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testPlayerPasses() throws Exception {
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(0, 300)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Player 1 passes
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(1, 0)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Player 1 tries to bid again
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(1, 350)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("Not your turn."))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void testPlayerPassesThenTriesToBidAgain() throws Exception {
        // Player 0 passes
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(0, 0)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Simulate other players making bids
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(1, 300)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(2, 320)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Player 0 tries to bid again after passing
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(0, 350)))  // or any other bid value
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("Not your turn."))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testTurnReturnsTo0thPlayerAfter5thPlayerBids() throws Exception {
        // Perform bids for all players
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(0, 300)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(1, 310)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(2, 320)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(3, 330)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(4, 340)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Player 5 bids
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(5, 345)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Check that the turn returns to player 0
        mockMvc.perform(get("/bidstate")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.currentPlayerId").value(0))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testSkippingPlayerAndTurnRotation() throws Exception {
        // Player 0 bids
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(0, 260)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Player 1 bids
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(1, 270)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Player 2 skips their turn
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(2, 0)))  // Passing
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Player 3 bids
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(3, 280)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Player 4 bids
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(4, 290)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Player 5 bids
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(5, 300)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Player 0 bids
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(0, 310)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Now Player 1 bids again
        mockMvc.perform(post("/bid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new BidRequest(1, 320)))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Check that the turn goes to Player 3
        mockMvc.perform(get("/bidstate")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.currentPlayerId").value(3))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testCallerCannotChooseOwnCards() throws Exception {
        // Set up the game and bidding state
        BidState bidState = BidState.getInstance();
        bidState.setBidFinish(true);
        bidState.setCallerPlayerId(1); // Set the caller player ID for the test

        // Define cards for the players
        Card cardFromCallerHand = new Card(Rank.ACE, Suit.HEARTS); // Card in the caller's hand
        Card validCardForOtherPlayer = new Card(Rank.KING, Suit.SPADES); // Card in another player's hand

        // Setup GameState with players
        GameState gameState = GameState.getInstance();
        List<Player> players = gameState.getPlayers();
        players.get(1).addInCurrentHand(cardFromCallerHand); // Add card to caller player hand
        players.get(2).addInCurrentHand(validCardForOtherPlayer); // Add card to another player hand

        // Attempt to set support cards with a card from the caller's hand (should fail)
        SupportCardSetRequest invalidRequest = new SupportCardSetRequest(1, Arrays.asList(cardFromCallerHand, validCardForOtherPlayer));

        mockMvc.perform(post("/setSupport")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.statusCode").value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.msg").value("Cannot request your own card."))
                .andDo(MockMvcResultHandlers.print());

        // Attempt to set support cards with valid choices (should succeed)
        Card anotherValidCard = new Card(Rank.QUEEN, Suit.CLUBS); // Another valid card
        players.get(2).addInCurrentHand(anotherValidCard); // Add another valid card to another player’s hand

        SupportCardSetRequest validRequest = new SupportCardSetRequest(1, Arrays.asList(validCardForOtherPlayer, anotherValidCard));

        mockMvc.perform(post("/setSupport")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // Verify that the bid state has been updated correctly
        mockMvc.perform(get("/bidstate")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstSupport.suit").value(Suit.SPADES.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstSupport.rank").value(Rank.KING.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.secondSupport.suit").value(Suit.CLUBS.name()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.secondSupport.rank").value(Rank.QUEEN.name()))
                .andDo(MockMvcResultHandlers.print());

        // Verify that the game state has been updated correctly
        mockMvc.perform(get("/gamestate")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.players[0].team").value(Team.ALLIES.name())) // Player 0
                .andExpect(MockMvcResultMatchers.jsonPath("$.players[1].team").value(Team.DEFENDERS.name())) // Player 1 (caller)
                .andExpect(MockMvcResultMatchers.jsonPath("$.players[2].team").value(Team.DEFENDERS.name())) // Player 2 (has one of the support cards)
                .andExpect(MockMvcResultMatchers.jsonPath("$.players[3].team").value(Team.ALLIES.name())) // Player 3
                .andExpect(MockMvcResultMatchers.jsonPath("$.players[4].team").value(Team.ALLIES.name())) // Player 4
                .andExpect(MockMvcResultMatchers.jsonPath("$.players[5].team").value(Team.ALLIES.name())); // Player 5
    }
}

