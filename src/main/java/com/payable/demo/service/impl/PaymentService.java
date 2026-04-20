package com.payable.demo.service.impl;

import com.payable.demo.dto.PaymentRequest;
import com.payable.demo.dto.PaymentResponse;
import com.payable.demo.exception.DuplicateRequestException;
import com.payable.demo.exception.ResourceNotFoundException;
import com.payable.demo.model.Order;
import com.payable.demo.model.PaymentTransaction;
import com.payable.demo.repository.OrderRepository;
import com.payable.demo.repository.PaymentTransactionRepository;
import com.payable.demo.service.INotificationService;
import com.payable.demo.service.IOrderService;
import com.payable.demo.service.IPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService implements IPaymentService {

    private final PaymentTransactionRepository paymentTransactionRepository;
    private final OrderRepository orderRepository;
    private final IOrderService orderService;
    private final INotificationService notificationService;

    // Idempotency cache as per guideline
    private final Map<String, Long> idempotencyCache = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        log.info("Processing payment for order: {}", request.getOrderId());

        // Idempotency check using cache
        if (idempotencyCache.containsKey(request.getIdempotencyKey())) {
            throw new DuplicateRequestException("Duplicate payment request for key: " + request.getIdempotencyKey());
        }

        // Database idempotency check
        paymentTransactionRepository.findByIdempotencyKey(request.getIdempotencyKey()).ifPresent(pt -> {
            throw new DuplicateRequestException("Payment already processed for key: " + request.getIdempotencyKey());
        });

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + request.getOrderId()));

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionRef(UUID.randomUUID().toString());
        transaction.setOrder(order);
        transaction.setAmount(request.getAmount());
        transaction.setCurrency(request.getCurrency());
        transaction.setStatus("SUCCESS"); // Simulating successful payment
        transaction.setPayerName(request.getPayerName());
        transaction.setPayerEmail(request.getPayerEmail());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setIdempotencyKey(request.getIdempotencyKey());
        transaction.setCreatedAt(Instant.now());
        transaction.setUpdatedAt(Instant.now());

        PaymentTransaction savedTransaction = paymentTransactionRepository.save(transaction);
        
        // Update order status
        orderService.updatePaymentStatus(order.getId(), "COMPLETED");
        orderService.updateOrderStatus(order.getId(), "PROCESSING");

        // Cache the idempotency key
        idempotencyCache.put(request.getIdempotencyKey(), savedTransaction.getId());

        notificationService.sendPaymentStatusUpdate(savedTransaction);
        log.info("Payment processed successfully: {}", savedTransaction.getTransactionRef());

        return mapToResponse(savedTransaction);
    }

    @Override
    public PaymentResponse getPaymentById(Long id) {
        PaymentTransaction transaction = paymentTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
        return mapToResponse(transaction);
    }

    @Override
    public PaymentResponse getPaymentByRef(String transactionRef) {
        PaymentTransaction transaction = paymentTransactionRepository.findByTransactionRef(transactionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + transactionRef));
        return mapToResponse(transaction);
    }

    @Override
    public Page<PaymentResponse> getAllPayments(String search, Pageable pageable) {
        Page<PaymentTransaction> transactions;
        if (search != null && !search.isEmpty()) {
            transactions = paymentTransactionRepository.findAll(search, pageable);
        } else {
            transactions = paymentTransactionRepository.findAll(pageable);
        }
        return transactions.map(this::mapToResponse);
    }

    @Override
    @Transactional
    public void updatePaymentStatus(Long id, String status, String failureReason) {
        PaymentTransaction transaction = paymentTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
        
        transaction.setStatus(status);
        transaction.setFailureReason(failureReason);
        transaction.setUpdatedAt(Instant.now());
        paymentTransactionRepository.save(transaction);
        
        notificationService.sendPaymentStatusUpdate(transaction);
    }

    private PaymentResponse mapToResponse(PaymentTransaction transaction) {
        return PaymentResponse.builder()
                .id(transaction.getId())
                .transactionRef(transaction.getTransactionRef())
                .orderId(transaction.getOrder().getId())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .status(transaction.getStatus())
                .payerName(transaction.getPayerName())
                .payerEmail(transaction.getPayerEmail())
                .paymentMethod(transaction.getPaymentMethod())
                .providerReference(transaction.getProviderReference())
                .failureReason(transaction.getFailureReason())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
