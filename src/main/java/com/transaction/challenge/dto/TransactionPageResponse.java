package com.transaction.challenge.dto;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "TransactionPageResponse", description = "Transaction Page Response")
public class TransactionPageResponse {
    @Schema(description = "Transactions", example = "List of transactions")
    List<TransactionDTO> transactions;
    @Schema(description = "Page", example = "0")
    int page;
    @Schema(description = "Size", example = "10")
    int size;
    @Schema(description = "Total Elements", example = "100")
    long totalElements;
    @Schema(description = "Total Pages", example = "10")
    int totalPages;
    @Schema(description = "Currency", example = "IDR")
    String currency;
    @Schema(description = "Total Credit", example = "100000.00")
    BigDecimal totalCredit;
    @Schema(description = "Total Debit", example = "100000.00")
    BigDecimal totalDebit;
}
