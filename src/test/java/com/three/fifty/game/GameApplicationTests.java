package com.three.fifty.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.three.fifty.models.PlayRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GameApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	void setup() {
		// Reset any state or setup if needed
	}

	@Test
	void testGameStateDealPlay() throws Exception {
		// Step 1: Call /gamestate
		mockMvc.perform(get("/gamestate"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.players").isArray())
				.andExpect(MockMvcResultMatchers.jsonPath("$.players.length()").value(6))
				.andExpect(MockMvcResultMatchers.jsonPath("$.currentPlayerId").isNumber());

		// Step 2: Call /deal
		mockMvc.perform(post("/deal"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.players").isArray())
				.andExpect(MockMvcResultMatchers.jsonPath("$.players.length()").value(6))
				.andExpect(MockMvcResultMatchers.jsonPath("$.players[0].currentHand").isArray())
				.andExpect(MockMvcResultMatchers.jsonPath("$.players[0].currentHand.length()").value(8));


		for(int i = 0; i < 6; i++){
			// Step 3: Play with a request body
			PlayRequest playRequest = new PlayRequest();
			playRequest.setPlayerId(i);
			playRequest.setCardNumber(0); // Adjust as needed for a valid card number
			mockMvc.perform(post("/play")
							.contentType(MediaType.APPLICATION_JSON)
							.content(objectMapper.writeValueAsString(playRequest)))
					.andExpect(status().isOk());
		}
		mockMvc.perform(get("/gamestate"))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.players").isArray())
				.andExpect(MockMvcResultMatchers.jsonPath("$.players.length()").value(6))
				.andExpect(MockMvcResultMatchers.jsonPath("$.currentPlayerId").isNumber());

	}
}
