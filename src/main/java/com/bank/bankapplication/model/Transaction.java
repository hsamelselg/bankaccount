package com.bank.bankapplication.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Transaction {
    private Long transactionId;
    private Long accountId;
    private BigDecimal amount;
    private String currency;
    private String direction;
    private String Description;
}
