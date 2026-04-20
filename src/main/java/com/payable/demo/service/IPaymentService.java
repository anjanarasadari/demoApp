package com.payable.demo.service;

import com.payable.demo.dto.PaymentRequest;
import com.payable.demo.dto.PaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPaymentService {
    PaymentResponse createPayment(PaymentRequest request);
    PaymentResponse getPaymentById(Long id);
    PaymentResponse getPaymentByRef(String transactionRef);
    Page<PaymentResponse> getAllPayments(String search, Pageable pageable);
    void updatePaymentStatus(Long id, String status, String failureReason);
}
