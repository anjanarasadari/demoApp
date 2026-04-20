package com.payable.demo.service.impl;

import com.payable.demo.dto.OrderItemRequest;
import com.payable.demo.dto.OrderRequest;
import com.payable.demo.dto.OrderResponse;
import com.payable.demo.exception.DuplicateRequestException;
import com.payable.demo.exception.ResourceNotFoundException;
import com.payable.demo.model.Order;
import com.payable.demo.model.OrderItem;
import com.payable.demo.model.Product;
import com.payable.demo.model.User;
import com.payable.demo.repository.OrderItemRepository;
import com.payable.demo.repository.OrderRepository;
import com.payable.demo.repository.ProductRepository;
import com.payable.demo.repository.UserRepository;
import com.payable.demo.service.IInventoryService;
import com.payable.demo.service.INotificationService;
import com.payable.demo.service.IOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final IInventoryService inventoryService;
    private final INotificationService notificationService;

    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Processing order creation for user: {}", request.getUserId());
        
        // Idempotency check
        orderRepository.findByIdempotencyKey(request.getIdempotencyKey()).ifPresent(o -> {
            throw new DuplicateRequestException("Order with this idempotency key already exists: " + request.getIdempotencyKey());
        });

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + request.getUserId()));

        Order order = new Order();
        order.setOrderRef(UUID.randomUUID().toString());
        order.setUser(user);
        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");
        order.setShippingAddress(request.getShippingAddress());
        order.setBillingAddress(request.getBillingAddress());
        order.setCurrency(request.getCurrency());
        order.setNote(request.getNote());
        order.setIdempotencyKey(request.getIdempotencyKey());
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());

        List<OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemReq.getProductId()));

            // Inventory check and reservation
            if (!inventoryService.isAvailable(product.getId(), itemReq.getQuantity())) {
                throw new com.payable.demo.exception.InsufficientInventoryException("Insufficient inventory for product: " + product.getName());
            }
            inventoryService.reserveInventory(product.getId(), itemReq.getQuantity());

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setSku(product.getSku());
            item.setProductName(product.getName());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(itemReq.getQuantity());
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            item.setLineTotal(lineTotal);
            item.setCreatedAt(Instant.now());

            items.add(item);
            totalAmount = totalAmount.add(lineTotal);
        }

        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(items);

        notificationService.sendOrderConfirmation(savedOrder);
        log.info("Order created successfully: {}", savedOrder.getOrderRef());

        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        return mapToResponse(order);
    }

    @Override
    public OrderResponse getOrderByRef(String orderRef) {
        Order order = orderRepository.findByOrderRef(orderRef)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderRef));
        return mapToResponse(order);
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        order.setStatus(status);
        order.setUpdatedAt(Instant.now());
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void updatePaymentStatus(Long id, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
        order.setPaymentStatus(status);
        order.setUpdatedAt(Instant.now());
        orderRepository.save(order);
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderRef(order.getOrderRef())
                .userId(order.getUser().getId())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .currency(order.getCurrency())
                .shippingAddress(order.getShippingAddress())
                .billingAddress(order.getBillingAddress())
                .paymentStatus(order.getPaymentStatus())
                .note(order.getNote())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
