package com.transaction.challenge.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import com.transaction.challenge.Repository.TransactionRepository;
import com.transaction.challenge.dto.TransactionDTO;
import com.transaction.challenge.dto.TransactionPageResponse;
import com.transaction.challenge.service.contract.FxRateService;
import com.transaction.challenge.service.contract.TransactionService;
import com.transaction.challenge.model.Transaction;
import com.transaction.challenge.model.TransactionTypeEnum;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final String baseCurrency;
    private final FxRateService fxRateService;

    @Autowired
    public TransactionServiceImpl(TransactionRepository transactionRepository, 
    @Value("${app.base-currency}") String baseCurrency,
    FxRateService fxRateService) {
        this.transactionRepository = transactionRepository;
        this.baseCurrency = baseCurrency;
        this.fxRateService = fxRateService;
    }

    @Override
    public TransactionPageResponse getTransactions(String customerId, LocalDate monthKey, Pageable pageable) {
        log.info("Fetching transactions: customer={}, month={}, page={}, size={}", 
            customerId, monthKey, pageable.getPageNumber(), pageable.getPageSize());
        
        var transactions = transactionRepository.findByCustomerIdAndMonthKey(customerId, monthKey, pageable);
        log.debug("Found {} transactions for customer {} in month {}", 
            transactions.getTotalElements(), customerId, monthKey);
        
        BigDecimal totalCredit = BigDecimal.ZERO;
        BigDecimal totalDebit = BigDecimal.ZERO;

        List<TransactionDTO> transactionDTOs = new ArrayList<>();       
        for (Transaction transaction : transactions.getContent()) {
            BigDecimal amount = transaction.getAmount();
            BigDecimal fxRate = fxRateService.getFxRate(transaction.getValueDate(), transaction.getCurrency(), baseCurrency);

            BigDecimal convertedAmount = amount.multiply(fxRate);

            if (transaction.getTransactionType() == TransactionTypeEnum.CREDIT) {
                totalCredit = totalCredit.add(convertedAmount);
            } else {
                totalDebit = totalDebit.add(convertedAmount);
            }

            TransactionDTO transactionDTO = new TransactionDTO();
            transactionDTO.setId(transaction.getId());
            transactionDTO.setAccountIban(transaction.getAccountIban());
            transactionDTO.setCurrency(baseCurrency);
            transactionDTO.setAmount(convertedAmount);
            transactionDTO.setValueDate(transaction.getValueDate());
            transactionDTO.setDescription(transaction.getDescription());    
            transactionDTO.setTransactionType(transaction.getTransactionType());
            transactionDTOs.add(transactionDTO);
        }

        log.info("Returning transaction page: items={}, totalCredit={}, totalDebit={}", 
            transactions.getNumberOfElements(), totalCredit, totalDebit);

        TransactionPageResponse response = new TransactionPageResponse();
        response.setTransactions(transactionDTOs);
        response.setPage(transactions.getNumber());
        response.setSize(transactions.getSize());
        response.setTotalElements(transactions.getTotalElements());
        response.setTotalPages(transactions.getTotalPages());
        response.setCurrency(baseCurrency);
        response.setTotalCredit(totalCredit);
        response.setTotalDebit(totalDebit);

        return response;
    }
    
}
