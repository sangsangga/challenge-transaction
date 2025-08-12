package com.transaction.challenge.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.transaction.challenge.Repository.TransactionRepository;
import com.transaction.challenge.model.Transaction;
import com.transaction.challenge.model.TransactionTypeEnum;
import com.transaction.challenge.model.event.TransactionEvent;
import com.transaction.challenge.service.contract.TransactionIngestionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j

@Service
public class TransactionIngestionServiceImpl  implements TransactionIngestionService {

    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionIngestionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public void ingestTransaction(TransactionEvent transactionEvent) {
        log.debug("Ingesting transaction: id={}, customer={}, amount={}", 
            transactionEvent.getId(), transactionEvent.getCustomerId(), transactionEvent.getCurrencyAmount());
        
        try {
            Parsed parsed = parseCurrencyAmount(transactionEvent.getCurrencyAmount().trim());
            log.debug("Parsed currency amount: {} {} {}", 
                parsed.currency(), parsed.amount(), parsed.type());
            
            Transaction transaction = new Transaction();
            transaction.setId(transactionEvent.getId());
            transaction.setCustomerId(transactionEvent.getCustomerId());
            transaction.setAccountIban(transactionEvent.getAccountIban());
            transaction.setTransactionType(parsed.type());
            transaction.setCurrency(parsed.currency());
            transaction.setAmount(parsed.amount());
            transaction.setValueDate(transactionEvent.getValueDate());
            transaction.setDescription(transactionEvent.getDescription());
            
            transactionRepository.save(transaction);
            log.info("Saved transaction: id={}, customer={}, type={}, amount={} {}", 
                transaction.getId(), transaction.getCustomerId(), 
                transaction.getTransactionType(), transaction.getAmount(), transaction.getCurrency());
                
        } catch (Exception e) {
            log.error("Failed to ingest transaction: id={}", transactionEvent.getId(), e);
            throw e;
        }
    }

    private Parsed parseCurrencyAmount(String currencyAmount) {
        String currency = currencyAmount.substring(0, currencyAmount.indexOf(" "));
        String numericPart = currencyAmount.substring(currencyAmount.indexOf(" ") + 1).replace("-", "");
        BigDecimal amount = new BigDecimal(numericPart);
        TransactionTypeEnum transactionType = currencyAmount.endsWith("-") ? TransactionTypeEnum.DEBIT : TransactionTypeEnum.CREDIT;

        return new Parsed(currency, amount, transactionType);
    }

    private record Parsed(String currency, BigDecimal amount, TransactionTypeEnum type) {}
}
