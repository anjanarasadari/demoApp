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
@Table(name = "orders", indexes = {
        @Index(name = "idx_orders_user_id",
                columnList = "user_id"),
        @Index(name = "idx_orders_status",
                columnList = "status"),
        @Index(name = "idx_orders_payment_status",
                columnList = "payment_status"),
        @Index(name = "idx_orders_created_at",
                columnList = "created_at")}, uniqueConstraints = {
        @UniqueConstraint(name = "uk_orders_order_ref",
                columnNames = {"order_ref"}),
        @UniqueConstraint(name = "uk_orders_idempotency_key",
                columnNames = {"idempotency_key"})})
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "order_ref", nullable = false, length = 36)
    private String orderRef;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ColumnDefault("'PENDING'")
    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @ColumnDefault("0.00")
    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @ColumnDefault("'LKR'")
    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "shipping_address", length = 500)
    private String shippingAddress;

    @Column(name = "billing_address", length = 500)
    private String billingAddress;

    @ColumnDefault("'PENDING'")
    @Column(name = "payment_status", nullable = false, length = 30)
    private String paymentStatus;

    @Column(name = "note", length = 1000)
    private String note;

    @Column(name = "idempotency_key", length = 100)
    private String idempotencyKey;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;


}