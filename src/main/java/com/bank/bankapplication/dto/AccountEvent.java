package com.bank.bankapplication.dto;

import com.bank.bankapplication.model.Balance;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountEvent {
    private String eventType;
    private Long accountId;
    private String customerId;
    private List<Balance> balances;
}
