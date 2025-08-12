package com.transaction.challenge;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.transaction.challenge.Repository.TransactionRepository;
import com.transaction.challenge.model.Transaction;
import com.transaction.challenge.model.TransactionTypeEnum;

@DataJpaTest
@ActiveProfiles("test")
public class TransactionRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction creditTransaction1;
    private Transaction creditTransaction2;
    private Transaction debitTransaction1;
    private Transaction debitTransaction2;
    private Transaction debitTransactionDifferentMonth;
    private Transaction transactionDifferentCustomer;

    private static final String CUSTOMER_ID = "123";
    private static final LocalDate MONTH_KEY = LocalDate.of(2024, 1, 1);

    @BeforeEach
    void setUp() {

        transactionRepository.deleteAll();
        testEntityManager.flush();
        testEntityManager.clear();

        creditTransaction1 = createTransaction(CUSTOMER_ID, MONTH_KEY, TransactionTypeEnum.CREDIT, BigDecimal.valueOf(100), "EUR", "Test transaction", LocalDate.of(2024, 1, 1));
        creditTransaction2 = createTransaction(CUSTOMER_ID, LocalDate.of(2024, 2, 1), TransactionTypeEnum.CREDIT, BigDecimal.valueOf(200), "EUR", "Test transaction", LocalDate.of(2024, 2, 1));
        debitTransaction1 = createTransaction(CUSTOMER_ID, MONTH_KEY, TransactionTypeEnum.DEBIT, BigDecimal.valueOf(100), "EUR", "Test transaction", LocalDate.of(2024, 1, 1));
        debitTransaction2 = createTransaction(CUSTOMER_ID, LocalDate.of(2024, 2, 1), TransactionTypeEnum.DEBIT, BigDecimal.valueOf(200), "EUR", "Test transaction", LocalDate.of(2024, 2, 1));
        debitTransactionDifferentMonth = createTransaction(CUSTOMER_ID, LocalDate.of(2024, 3, 1), TransactionTypeEnum.DEBIT, BigDecimal.valueOf(300), "EUR", "Test transaction", LocalDate.of(2024, 3, 1));
        transactionDifferentCustomer = createTransaction("456", LocalDate.of(2024, 1, 1), TransactionTypeEnum.CREDIT, BigDecimal.valueOf(100), "EUR", "Test transaction", LocalDate.of(2024, 1, 1));

        testEntityManager.persist(creditTransaction1);
        testEntityManager.persist(creditTransaction2);
        testEntityManager.persist(debitTransaction1);
        testEntityManager.persist(debitTransaction2);
        testEntityManager.persist(debitTransactionDifferentMonth);
        testEntityManager.persist(transactionDifferentCustomer);

        testEntityManager.flush();
        testEntityManager.clear();
    } 

    private Transaction createTransaction(
        String customerId, LocalDate monthKey, TransactionTypeEnum transactionType,
        BigDecimal amount, String currency, String description, LocalDate valueDate
        ) {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setCustomerId(customerId);
        transaction.setMonthKey(monthKey);
        transaction.setTransactionType(transactionType);
        transaction.setAmount(amount);
        transaction.setCurrency(currency);
        transaction.setDescription(description);
        transaction.setValueDate(valueDate);
        return transaction;
    }

    @Test
    @DisplayName("Should find transactions by customer ID and month key")
    void testFindByCustomerIdAndMonthKey_shouldReturnTransactionsForCustomerAndMonth() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("valueDate").descending());
        Page<Transaction> result = transactionRepository.findByCustomerIdAndMonthKey(CUSTOMER_ID, MONTH_KEY, pageable);
        
        
        assertThat(result.getContent()).hasSize(2);

        List<UUID> transactionIds = result.getContent().stream().map(Transaction::getId).collect(Collectors.toList());
        assertThat(transactionIds).contains(creditTransaction1.getId(), debitTransaction1.getId());

        assertThat(transactionIds).doesNotContain(debitTransactionDifferentMonth.getId());
        assertThat(transactionIds).doesNotContain(transactionDifferentCustomer.getId());

        assertThat(result.getContent()).extracting(Transaction::getCustomerId).containsOnly(CUSTOMER_ID);
        assertThat(result.getContent()).extracting(Transaction::getMonthKey).containsOnly(MONTH_KEY);
        assertThat(result.getContent()).extracting(Transaction::getTransactionType).containsOnly(TransactionTypeEnum.CREDIT, TransactionTypeEnum.DEBIT);
        assertThat(result.getContent()).extracting(Transaction::getAmount).containsOnly(new BigDecimal("100.00"), new BigDecimal("100.00"));
    }
    
}
