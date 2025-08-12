package com.transaction.challenge.service.contract;

import com.transaction.challenge.model.event.TransactionEvent;

public interface TransactionIngestionService {
    void ingestTransaction(TransactionEvent transactionEvent);
}   