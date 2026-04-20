package com.payable.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "inventory_transactions", indexes = {
        @Index(name = "idx_inventory_transactions_product_id",
                columnList = "product_id"),
        @Index(name = "idx_inventory_transactions_type",
                columnList = "transaction_type"),
        @Index(name = "idx_inventory_transactions_created_at",
                columnList = "created_at")}, uniqueConstraints = {@UniqueConstraint(name = "uk_inventory_transactions_ref",
        columnNames = {"transaction_ref"})})
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "transaction_ref", nullable = false, length = 36)
    private String transactionRef;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "transaction_type", nullable = false, length = 30)
    private String transactionType;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "reference_type", length = 30)
    private String referenceType;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "note", length = 500)
    private String note;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


}