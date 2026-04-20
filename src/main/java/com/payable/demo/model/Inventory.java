package com.payable.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "inventory", uniqueConstraints = {@UniqueConstraint(name = "uk_inventory_product",
        columnNames = {"product_id"})})
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ColumnDefault("0")
    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity;

    @ColumnDefault("0")
    @Column(name = "reserved_quantity", nullable = false)
    private Integer reservedQuantity;

    @ColumnDefault("0")
    @Column(name = "reorder_level", nullable = false)
    private Integer reorderLevel;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "last_updated_at", nullable = false)
    private Instant lastUpdatedAt;


}