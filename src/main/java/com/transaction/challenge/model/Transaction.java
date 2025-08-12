package com.transaction.challenge.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transactions")
@Getter
@Setter
public class Transaction extends BaseEntity {
    @Id
    private UUID id;

    private String customerId;
    private String accountIban;
    private String currency;
    private BigDecimal amount;
    private LocalDate valueDate;
    private String description;
    private LocalDate monthKey;

    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum transactionType;
}



