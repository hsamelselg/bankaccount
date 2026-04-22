package com.bank.bankapplication.controller;

import com.bank.bankapplication.dto.CreateAccountRequest;
import com.bank.bankapplication.dto.TransactionRequest;
import com.bank.bankapplication.model.Account;
import com.bank.bankapplication.model.Transaction;
import com.bank.bankapplication.service.AccountService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public  Account createAccount(@RequestBody CreateAccountRequest request) {
        return accountService.createAccount(request);
    }

    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id) {
        return accountService.getAccount(id);
    }

    @PostMapping("/transactions")
    public Transaction createTransaction(@RequestBody TransactionRequest request) {
        return accountService.createTransaction(request);
    }
}
