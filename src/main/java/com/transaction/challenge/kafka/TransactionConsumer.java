package com.transaction.challenge.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.transaction.challenge.model.event.TransactionEvent;
import com.transaction.challenge.service.contract.TransactionIngestionService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class TransactionConsumer {
    private final ObjectMapper objectMapper;
    private final TransactionIngestionService transactionIngestionService;

    @Autowired
    public TransactionConsumer(TransactionIngestionService transactionIngestionService, ObjectMapper objectMapper) {
        this.transactionIngestionService = transactionIngestionService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "transactions", groupId = "transaction-group")
    public void onMessage(String message) throws JsonProcessingException {
        log.info("Received transaction message: key={}, payload={}", 
            message.substring(0, Math.min(100, message.length())));
        
        try {
            TransactionEvent event = objectMapper.readValue(message, TransactionEvent.class);
            log.debug("Parsed transaction event: id={}, customerId={}, amount={}", 
                event.getId(), event.getCustomerId(), event.getCurrencyAmount());
            
            transactionIngestionService.ingestTransaction(event);
            log.info("Successfully ingested transaction: id={}", event.getId());
        } catch (Exception e) {
            log.error("Failed to process transaction message: {}", message, e);
            throw e;
        }
    }
}
