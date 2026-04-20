package com.payable.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "payment_transactions", indexes = {
        @Index(name = "idx_payment_transactions_order_id",
                columnList = "order_id"),
        @Index(name = "idx_payment_transactions_status_created",
                columnList = "status, created_at"),
        @Index(name = "idx_payment_transactions_payer_email",
                columnList = "payer_email"),
        @Index(name = "idx_payment_transactions_created_at",
                columnList = "created_at")}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_payment_transactions_ref",
                columnNames = {"transaction_ref"}),
        @UniqueConstraint(name = "uk_payment_transactions_idempotency",
                columnNames = {"idempotency_key"})})
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "transaction_ref", nullable = false, length = 36)
    private String transactionRef;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @ColumnDefault("'LKR'")
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @ColumnDefault("'PENDING'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "payer_name", nullable = false, length = 100)
    private String payerName;

    @Column(name = "payer_email", nullable = false)
    private String payerEmail;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "provider_reference", length = 100)
    private String providerReference;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "idempotency_key", length = 100)
    private String idempotencyKey;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


}