package com.payable.demo.service.impl;

import com.payable.demo.dto.InventoryResponse;
import com.payable.demo.dto.InventoryUpdateRequest;
import com.payable.demo.exception.InsufficientInventoryException;
import com.payable.demo.exception.ResourceNotFoundException;
import com.payable.demo.model.Inventory;
import com.payable.demo.repository.InventoryRepository;
import com.payable.demo.repository.ProductRepository;
import com.payable.demo.service.IInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryService implements IInventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    
    // Inventory cache for available quantity as per guideline
    private final Map<Long, Integer> inventoryCache = new ConcurrentHashMap<>();

    @Override
    public boolean isAvailable(Long productId, Integer quantity) {
        Integer available = inventoryCache.computeIfAbsent(productId, id -> 
            inventoryRepository.findByProductId(id)
                .map(Inventory::getAvailableQuantity)
                .orElse(0)
        );
        return available >= quantity;
    }

    @Override
    @Transactional
    public void reserveInventory(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientInventoryException("Insufficient inventory for product: " + productId);
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
        inventory.setLastUpdatedAt(Instant.now());
        inventoryRepository.save(inventory);
        
        // Update cache
        inventoryCache.put(productId, inventory.getAvailableQuantity());
        log.info("Reserved {} units for product {}", quantity, productId);
    }

    @Override
    @Transactional
    public void releaseInventory(Long productId, Integer quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        inventory.setReservedQuantity(Math.max(0, inventory.getReservedQuantity() - quantity));
        inventory.setLastUpdatedAt(Instant.now());
        inventoryRepository.save(inventory);
        
        // Update cache
        inventoryCache.put(productId, inventory.getAvailableQuantity());
        log.info("Released {} units for product {}", quantity, productId);
    }

    @Override
    @Transactional
    public void updateStock(InventoryUpdateRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                .orElseGet(() -> {
                    Inventory newInv = new Inventory();
                    newInv.setProduct(productRepository.findById(request.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + request.getProductId())));
                    newInv.setAvailableQuantity(0);
                    newInv.setReservedQuantity(0);
                    newInv.setReorderLevel(10);
                    return newInv;
                });

        inventory.setAvailableQuantity(request.getQuantity());
        inventory.setLastUpdatedAt(Instant.now());
        inventoryRepository.save(inventory);
        
        // Update cache
        inventoryCache.put(request.getProductId(), inventory.getAvailableQuantity());
        log.info("Updated stock for product {} to {}", request.getProductId(), request.getQuantity());
    }

    @Override
    public InventoryResponse getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));
        
        return mapToResponse(inventory);
    }

    @Override
    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private InventoryResponse mapToResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProduct().getId())
                .productName(inventory.getProduct().getName())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .reorderLevel(inventory.getReorderLevel())
                .lastUpdatedAt(inventory.getLastUpdatedAt())
                .build();
    }
}
