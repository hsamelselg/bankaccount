package com.bank.bankapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountEvent {
    private String eventType;
    private Long accountId;
    private String costumerId;
}
