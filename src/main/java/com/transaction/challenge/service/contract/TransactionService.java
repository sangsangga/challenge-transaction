package com.transaction.challenge.service.contract;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

import com.transaction.challenge.dto.TransactionPageResponse;

public interface TransactionService {
    TransactionPageResponse getTransactions(String customerId, LocalDate monthKey, Pageable pageable);
    
}
