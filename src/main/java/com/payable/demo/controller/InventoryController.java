package com.payable.demo.controller;

import com.payable.demo.dto.ApiErrorResponse;
import com.payable.demo.dto.InventoryResponse;
import com.payable.demo.dto.InventoryUpdateRequest;
import com.payable.demo.service.IInventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory management endpoints")
public class InventoryController {

    private final IInventoryService inventoryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE', 'SUPPORT')")
    @Operation(summary = "Get all inventory items", description = "Returns a list of all inventory records.")
    @ApiResponse(responseCode = "200", description = "List of inventory items retrieved successfully",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = InventoryResponse.class))))
    public ResponseEntity<List<InventoryResponse>> getAll() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE', 'SUPPORT', 'CUSTOMER')")
    @Operation(summary = "Get inventory by product ID", description = "Retrieves inventory details for a specific product.")
    @ApiResponse(responseCode = "200", description = "Inventory found",
            content = @Content(schema = @Schema(implementation = InventoryResponse.class)))
    @ApiResponse(responseCode = "404", description = "Inventory not found for product",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<InventoryResponse> getByProductId(
            @Parameter(description = "ID of the product to retrieve inventory for") @PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getInventoryByProductId(productId));
    }

    @GetMapping("/check")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE', 'SUPPORT', 'CUSTOMER')")
    @Operation(summary = "Check availability", description = "Checks if the requested quantity of a product is available in inventory.")
    @ApiResponse(responseCode = "200", description = "Availability checked successfully",
            content = @Content(schema = @Schema(implementation = Boolean.class)))
    public ResponseEntity<Boolean> checkAvailability(
            @Parameter(description = "ID of the product") @RequestParam Long productId,
            @Parameter(description = "Quantity to check") @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.isAvailable(productId, quantity));
    }

    @PutMapping("/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE')")
    @Operation(summary = "Update stock levels", description = "Updates the available stock quantity for a product. If product doesn't have inventory record, it creates one.")
    @ApiResponse(responseCode = "200", description = "Stock updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Product not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<Void> updateStock(@Valid @RequestBody InventoryUpdateRequest request) {
        inventoryService.updateStock(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reserve")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE', 'SUPPORT', 'CUSTOMER')")
    @Operation(summary = "Reserve inventory", description = "Reserves a specified quantity of a product, moving it from available to reserved.")
    @ApiResponse(responseCode = "200", description = "Inventory reserved successfully")
    @ApiResponse(responseCode = "400", description = "Insufficient inventory",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Inventory not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<Void> reserveInventory(
            @Parameter(description = "ID of the product") @RequestParam Long productId,
            @Parameter(description = "Quantity to reserve") @RequestParam Integer quantity) {
        inventoryService.reserveInventory(productId, quantity);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/release")
    @PreAuthorize("hasAnyRole('ADMIN', 'WAREHOUSE', 'SUPPORT', 'CUSTOMER')")
    @Operation(summary = "Release inventory", description = "Releases reserved inventory back to available stock.")
    @ApiResponse(responseCode = "200", description = "Inventory released successfully")
    @ApiResponse(responseCode = "404", description = "Inventory not found",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<Void> releaseInventory(
            @Parameter(description = "ID of the product") @RequestParam Long productId,
            @Parameter(description = "Quantity to release") @RequestParam Integer quantity) {
        inventoryService.releaseInventory(productId, quantity);
        return ResponseEntity.ok().build();
    }
}
