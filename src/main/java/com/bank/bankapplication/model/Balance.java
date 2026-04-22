package com.bank.bankapplication.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Balance {
    private BigDecimal availabeAmount;
    private String currency;
}
