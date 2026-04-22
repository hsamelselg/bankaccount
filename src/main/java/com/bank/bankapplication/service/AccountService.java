package com.bank.bankapplication.service;

import com.bank.bankapplication.config.RabbitMQConfig;
import com.bank.bankapplication.dto.AccountEvent;
import com.bank.bankapplication.dto.CreateAccountRequest;
import com.bank.bankapplication.dto.TransactionRequest;
import com.bank.bankapplication.mapper.AccountMapper;
import com.bank.bankapplication.model.Account;
import com.bank.bankapplication.model.Balance;
import com.bank.bankapplication.model.Transaction;
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

    public Transaction createTransaction(TransactionRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount: " + request.getAmount());
        }
        if (request.getDescription() == null || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("Missing description: " + request.getDescription());
        }
        if (!"IN".equals(request.getDirection()) && !"OUT".equals(request.getDirection())) {
            throw new IllegalArgumentException("Invalid direction: " + request.getDirection());
        }

        Account account = accountMapper.findAccountById(request.getAccountId());
        if (account == null) {
            throw new RuntimeException("Account not found with ID: " +  request.getAccountId());
        }

        Balance targetBalance = account.getBalances().stream()
                .filter(b -> b.getCurrency().equals(request.getCurrency()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Account does not have currency: " + request.getCurrency()));

        if ("OUT".equals(request.getDirection()) &&  targetBalance.getAvailableAmount().compareTo(request.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        BigDecimal multiplier = "IN".equals(request.getDirection()) ? BigDecimal.ONE : new BigDecimal("-1");
        BigDecimal amountChange = request.getAmount().multiply(multiplier);

        Transaction transaction = new Transaction();
        transaction.setAccountId(account.getAccountId());
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setDirection(request.getDirection());
        transaction.setDescription(request.getDescription());

        accountMapper.insertTransaction(transaction);
        accountMapper.updateBalance(request.getAccountId(), request.getCurrency(), amountChange);

        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "account.transaction", transaction);

        return transaction;
    }
}
