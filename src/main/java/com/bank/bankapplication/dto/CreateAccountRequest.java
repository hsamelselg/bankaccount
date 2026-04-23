package com.bank.bankapplication.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateAccountRequest {
    private String customerId;
    private String country;
    private List<String> currencies;
}
