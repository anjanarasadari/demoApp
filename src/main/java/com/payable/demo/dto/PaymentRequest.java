package com.payable.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    private String currency;

    @NotBlank(message = "Payer name is required")
    private String payerName;

    @NotBlank(message = "Payer email is required")
    @Email(message = "Invalid email format")
    private String payerEmail;

    private String paymentMethod;
    
    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;
}
