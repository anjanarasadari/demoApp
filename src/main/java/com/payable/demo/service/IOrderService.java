package com.payable.demo.service;

import com.payable.demo.dto.OrderRequest;
import com.payable.demo.dto.OrderResponse;

import java.util.List;

public interface IOrderService {
    OrderResponse createOrder(OrderRequest request);
    OrderResponse getOrderById(Long id);
    OrderResponse getOrderByRef(String orderRef);
    List<OrderResponse> getAllOrders();
    void updateOrderStatus(Long id, String status);
    void updatePaymentStatus(Long id, String status);
}
