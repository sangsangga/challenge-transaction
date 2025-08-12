package com.transaction.challenge.Repository;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;

import com.transaction.challenge.model.Transaction;
import com.transaction.challenge.model.TransactionTypeEnum;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    @Query("SELECT t FROM Transaction t WHERE t.customerId = :customerId AND t.monthKey = :monthKey")
    Page<Transaction> findByCustomerIdAndMonthKey(@Param("customerId") String customerId, @Param("monthKey") LocalDate monthKey, Pageable pageable);

    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.customerId = :customerId AND t.monthKey = :monthKey AND t.transactionType = :transactionType")
    BigDecimal getTotalAmountByCustomerIdAndMonthKeyAndTransactionType(@Param("customerId") String customerId, @Param("monthKey") LocalDate monthKey, @Param("transactionType") TransactionTypeEnum transactionType);
}
