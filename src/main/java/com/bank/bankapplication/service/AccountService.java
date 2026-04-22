package com.bank.bankapplication.service;

import com.bank.bankapplication.config.RabbitMQConfig;
import com.bank.bankapplication.dto.AccountEvent;
import com.bank.bankapplication.dto.CreateAccountRequest;
import com.bank.bankapplication.mapper.AccountMapper;
import com.bank.bankapplication.model.Account;
import com.bank.bankapplication.model.Balance;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class AccountService {
    private final AccountMapper accountMapper;
    private final Set<String> ALLOWED_CURRENCIES = Set.of("EUR", "SEK", "GBP", "USD");

    private final RabbitTemplate rabbitTemplate;

    public AccountService(AccountMapper accountMapper, RabbitTemplate rabbitTemplate) {
        this.accountMapper = accountMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public Account createAccount(CreateAccountRequest request) {
        for (String currency : request.getCurrencies()) {
            if (!ALLOWED_CURRENCIES.contains(currency)) {
                throw new IllegalArgumentException("Invalid currency: " + currency);
            }
        }
        Account account = new Account();
        account.setCustomerId(request.getCostumerId());
        account.setCountry(request.getCountry());

        accountMapper.insertAccount(account);

        AccountEvent event = new AccountEvent("ACCOUNT_CREATED", account.getAccountId(), account.getCostumerId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "account.event.created", event);

        List<Balance> balances = new ArrayList<>();
        for (String currency : request.getCurrencies()) {
            Balance balance = new Balance();
            balance.setCurrency(currency);
            balance.setAvailableAmount(BigDecimal.ZERO);

            accountMapper.insertBalance(account.getAccountId(), balance.getAvailableAmount(), currency);
            balances.add(balance);
        }

        account.setBalances(balances);
        return account;
    }

    public Account getAccount(Long accountId) {
        Account account = accountMapper.findAccountById(accountId);
        if (account == null) {
            throw new RuntimeException("Account not found with ID: " +  accountId);
        }
        return account;
    }
}
