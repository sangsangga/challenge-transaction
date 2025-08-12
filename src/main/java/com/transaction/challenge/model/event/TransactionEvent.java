package com.transaction.challenge.model.event;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class TransactionEvent {
    private UUID id;
    private String customerId;
    private String accountIban;
    private String currencyAmount;
    private LocalDate valueDate;
    private String description;
}
