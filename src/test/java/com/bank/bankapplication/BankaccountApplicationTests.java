package com.bank.bankapplication;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.*;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
class BankaccountApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private tools.jackson.databind.ObjectMapper objectMapper; // Kasutame seda JSON-i lugemiseks

	@MockitoBean
	private RabbitTemplate rabbitTemplate;

	@Test
	void CreateAccountAndTransactionTest() throws Exception {
		// 1. LOO KONTO
		String accountJson = "{\"customerId\": \"test_user\", \"country\": \"Estonia\", \"currencies\": [\"EUR\"]}";

		String response = mockMvc.perform(post("/accounts")
						.contentType(MediaType.APPLICATION_JSON)
						.content(accountJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.customerId").value("test_user"))
				.andReturn().getResponse().getContentAsString();

		// PARANDATUD: Kasutame objectMapperit, et lugeda ID-d
		tools.jackson.databind.JsonNode root = objectMapper.readTree(response);
		Long accountId = root.path("accountId").asLong();

		// 2. TEE TEHING
		String transactionJson = String.format(
				"{\"accountId\": %d, \"amount\": 50.0, \"currency\": \"EUR\", \"direction\": \"IN\", \"description\": \"Test\"}",
				accountId
		);

		mockMvc.perform(post("/accounts/transactions")
						.contentType(MediaType.APPLICATION_JSON)
						.content(transactionJson))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.balanceAfterTransaction").value(50.0));

		// 3. KONTROLLI GET MEETODIT
		mockMvc.perform(get("/accounts/" + accountId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.balances[0].availableAmount").value(50.0));
	}

	@Test
	void InsufficientFundsTest() throws Exception {
		String transactionJson = "{\"accountId\":1, \"amount\":10000.0, \"currency\": \"EUR\", \"direction\": \"OUT\", \"description\": \"Test\"}";

		assertThrows(jakarta.servlet.ServletException.class, () -> {
			mockMvc.perform(post("/accounts/transactions")
					.contentType(MediaType.APPLICATION_JSON)
					.content(transactionJson));
		});
	}

	@Test
	void CreateAccountInvalidCurrencyTest() throws Exception {
		String accountJson = "{\"customerId\": \"Mike\", \"country\": \"Estonia\", \"currencies\": [\"BTC\"]}";
		assertThrows(jakarta.servlet.ServletException.class, () -> {
			mockMvc.perform(post("/accounts")
			.contentType(MediaType.APPLICATION_JSON)
					.content(accountJson));
		});
	}

	@Test
	void GetAccountNotFoundTest() throws Exception {
		Long accountId = -1L;

		assertThrows(jakarta.servlet.ServletException.class, () -> {
			mockMvc.perform(get("/accounts/" + accountId));
		});
	}
}
