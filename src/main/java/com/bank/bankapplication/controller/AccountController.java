package com.bank.bankapplication.controller;

import com.bank.bankapplication.dto.CreateAccountRequest;
import com.bank.bankapplication.model.Account;
import com.bank.bankapplication.service.AccountService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    public  Account createAccount(@RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }
}
