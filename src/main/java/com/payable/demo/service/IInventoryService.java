package com.payable.demo.service;

import com.payable.demo.dto.InventoryResponse;
import com.payable.demo.dto.InventoryUpdateRequest;

import java.util.List;

public interface IInventoryService {
    boolean isAvailable(Long productId, Integer quantity);
    void reserveInventory(Long productId, Integer quantity);
    void releaseInventory(Long productId, Integer quantity);
    void updateStock(InventoryUpdateRequest request);
    InventoryResponse getInventoryByProductId(Long productId);
    List<InventoryResponse> getAllInventory();
}
