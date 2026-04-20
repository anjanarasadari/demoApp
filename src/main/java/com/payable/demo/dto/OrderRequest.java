package com.payable.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    @NotNull(message = "User ID is required")
    private Long userId;

    @NotEmpty(message = "Order items cannot be empty")
    private List<OrderItemRequest> items;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    @NotBlank(message = "Billing address is required")
    private String billingAddress;

    private String currency = "LKR";

    @NotBlank(message = "Idempotency key is required")
    private String idempotencyKey;

    private String note;
}
