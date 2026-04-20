package com.payable.demo.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private String transactionRef;
    private Long orderId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String payerName;
    private String payerEmail;
    private String paymentMethod;
    private String providerReference;
    private String failureReason;
    private Instant createdAt;
}
