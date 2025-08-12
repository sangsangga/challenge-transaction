package com.transaction.challenge.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.transaction.challenge.model.TransactionTypeEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "TransactionDTO", description = "Transaction DTO")
public class TransactionDTO {
    @Schema(description = "Transaction ID", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id;
    @Schema(description = "Account IBAN", example = "CH93-0000-0000-0000-0000-0")
    String accountIban;
    @Schema(description = "Currency", example = "IDR")
    String currency;
    @Schema(description = "Amount", example = "100000.00")
    BigDecimal amount;
    @Schema(description = "Value Date", example = "2021-01-01")
    LocalDate valueDate;
    @Schema(description = "Description", example = "Online payment CHF")
    String description;
    @Schema(description = "Transaction Type", example = "CREDIT")
    TransactionTypeEnum transactionType;
}
