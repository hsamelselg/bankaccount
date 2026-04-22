package com.bank.bankapplication.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateAccountRequest {
    private String costumerId;
    private String country;
    private List<String> currencies;

    public void setCustomerId(String id) {
        this.costumerId = id;
    }
}
