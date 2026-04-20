package com.payable.demo.repository;

import com.payable.demo.model.PaymentTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByTransactionRef(String transactionRef);
    Optional<PaymentTransaction> findByIdempotencyKey(String idempotencyKey);

    @Query("SELECT pt FROM PaymentTransaction pt WHERE " +
           "LOWER(pt.payerName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(pt.payerEmail) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(pt.transactionRef) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<PaymentTransaction> findAll(String search, Pageable pageable);
}
