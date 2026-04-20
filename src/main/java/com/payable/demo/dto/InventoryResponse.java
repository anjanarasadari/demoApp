package com.payable.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class InventoryResponse {
    private Long id;
    private Long productId;
    private String productName;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    private Integer reorderLevel;
    private Instant lastUpdatedAt;
}
