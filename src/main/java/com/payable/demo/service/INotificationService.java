package com.payable.demo.service;

import com.payable.demo.model.Order;
import com.payable.demo.model.PaymentTransaction;

public interface INotificationService {
    void sendOrderConfirmation(Order order);
    void sendPaymentStatusUpdate(PaymentTransaction transaction);
    void sendFailureAlert(String message, String recipient);
}
