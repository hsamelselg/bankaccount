package com.bank.bankapplication.model;

import lombok.Data;
import java.util.List;

@Data
public class Account {
    private Long accountId;
    private String costumerId;
    private String country;
    private List<Balance> balances;

    public void setCustomerId(String number) {
        this.costumerId=number;
    }
}
