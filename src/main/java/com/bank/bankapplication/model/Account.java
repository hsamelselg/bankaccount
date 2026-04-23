package com.bank.bankapplication.model;

import lombok.Data;
import java.util.List;

@Data
public class Account {
    private Long accountId;
    private String customerId;
    private String country;
    private List<Balance> balances;

}
