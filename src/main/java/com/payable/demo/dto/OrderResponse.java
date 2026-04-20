package com.payable.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderRef;
    private Long userId;
    private String status;
    private BigDecimal totalAmount;
    private String currency;
    private String shippingAddress;
    private String billingAddress;
    private String paymentStatus;
    private String note;
    private Instant createdAt;
}
