package com.bank.bankapplication;

import com.bank.bankapplication.controller.AccountController;
import com.bank.bankapplication.dto.CreateAccountRequest;
import com.bank.bankapplication.mapper.AccountMapper;
import com.bank.bankapplication.model.Account;
import com.bank.bankapplication.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class BankaccountApplication {


	public static void main(String[] args) {
		SpringApplication.run(BankaccountApplication.class, args);
		System.out.println("Bank Account Application Started");
	}

	@Bean
	CommandLineRunner testDatabase(AccountService accountService) {
		return args -> {
			System.out.println("--- TESTING SERVICE LOGIC ---");
			CreateAccountRequest req = new CreateAccountRequest();
			req.setCustomerId("user_99");
			req.setCountry("Estonia");
			req.setCurrencies(List.of("EUR", "GBP"));

			Account result = accountService.createAccount(req);
			System.out.println("Created Account ID: " + result.getAccountId());
			System.out.println("Balances Created: " + result.getBalances().size());
		};
	}
}
