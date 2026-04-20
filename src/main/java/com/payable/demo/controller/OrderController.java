package com.payable.demo.controller;

import com.payable.demo.dto.ApiErrorResponse;
import com.payable.demo.dto.OrderRequest;
import com.payable.demo.dto.OrderResponse;
import com.payable.demo.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    private final IOrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Create a new order", description = "Creates a new order, reserves inventory, and sends confirmation.")
    @ApiResponse(responseCode = "201", description = "Order created successfully",
            content = @Content(schema = @Schema(implementation = OrderResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request data or insufficient inventory",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "User or product not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Duplicate order request",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT', 'WAREHOUSE', 'CUSTOMER')")
    @Operation(summary = "Get order by ID", description = "Retrieves details of a specific order by its ID.")
    @ApiResponse(responseCode = "200", description = "Order found",
            content = @Content(schema = @Schema(implementation = OrderResponse.class)))
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<OrderResponse> getById(
            @Parameter(description = "ID of the order to retrieve") @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/ref/{orderRef}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT', 'WAREHOUSE', 'CUSTOMER')")
    @Operation(summary = "Get order by reference", description = "Retrieves details of a specific order by its unique reference.")
    @ApiResponse(responseCode = "200", description = "Order found",
            content = @Content(schema = @Schema(implementation = OrderResponse.class)))
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<OrderResponse> getByRef(
            @Parameter(description = "Reference of the order to retrieve") @PathVariable String orderRef) {
        return ResponseEntity.ok(orderService.getOrderByRef(orderRef));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT', 'WAREHOUSE')")
    @Operation(summary = "Get all orders", description = "Returns a list of all orders.")
    @ApiResponse(responseCode = "200", description = "List of orders retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = OrderResponse.class))))
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE', 'SUPPORT')")
    @Operation(summary = "Update order status", description = "Updates the status of an existing order.")
    @ApiResponse(responseCode = "200", description = "Order status updated successfully")
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<Void> updateStatus(
            @Parameter(description = "ID of the order to update") @PathVariable Long id,
            @Parameter(description = "New status of the order") @RequestParam String status) {
        orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/payment-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    @Operation(summary = "Update payment status", description = "Updates the payment status of an existing order.")
    @ApiResponse(responseCode = "200", description = "Payment status updated successfully")
    @ApiResponse(responseCode = "404", description = "Order not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<Void> updatePaymentStatus(
            @Parameter(description = "ID of the order to update") @PathVariable Long id,
            @Parameter(description = "New payment status of the order") @RequestParam String status) {
        orderService.updatePaymentStatus(id, status);
        return ResponseEntity.ok().build();
    }
}
