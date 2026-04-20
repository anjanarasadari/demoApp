package com.payable.demo.service.impl;

import com.payable.demo.model.NotificationLog;
import com.payable.demo.model.Order;
import com.payable.demo.model.PaymentTransaction;
import com.payable.demo.repository.NotificationRepository;
import com.payable.demo.service.INotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService implements INotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void sendOrderConfirmation(Order order) {
        NotificationLog notification = new NotificationLog();
        notification.setNotificationRef(UUID.randomUUID().toString());
        notification.setOrder(order);
        notification.setUser(order.getUser());
        notification.setChannel("EMAIL");
        notification.setNotificationType("ORDER_CONFIRMATION");
        notification.setRecipient(order.getUser().getEmail());
        notification.setSubject("Order Confirmation - " + order.getOrderRef());
        notification.setMessage("Your order has been placed successfully. Order Ref: " + order.getOrderRef());
        notification.setStatus("SENT");
        notification.setSentAt(Instant.now());
        notification.setCreatedAt(Instant.now());

        notificationRepository.save(notification);
        log.info("Order confirmation sent for order: {}", order.getOrderRef());
    }

    @Override
    public void sendPaymentStatusUpdate(PaymentTransaction transaction) {
        NotificationLog notification = new NotificationLog();
        notification.setNotificationRef(UUID.randomUUID().toString());
        notification.setOrder(transaction.getOrder());
        notification.setPaymentTransaction(transaction);
        notification.setUser(transaction.getOrder().getUser());
        notification.setChannel("EMAIL");
        notification.setNotificationType("PAYMENT_STATUS_UPDATE");
        notification.setRecipient(transaction.getPayerEmail());
        notification.setSubject("Payment Update - " + transaction.getTransactionRef());
        notification.setMessage("Your payment status has been updated to: " + transaction.getStatus());
        notification.setStatus("SENT");
        notification.setSentAt(Instant.now());
        notification.setCreatedAt(Instant.now());

        notificationRepository.save(notification);
        log.info("Payment status update notification sent for transaction: {}", transaction.getTransactionRef());
    }

    @Override
    public void sendFailureAlert(String message, String recipient) {
        NotificationLog notification = new NotificationLog();
        notification.setNotificationRef(UUID.randomUUID().toString());
        notification.setChannel("SYSTEM");
        notification.setNotificationType("FAILURE_ALERT");
        notification.setRecipient(recipient);
        notification.setSubject("System Failure Alert");
        notification.setMessage(message);
        notification.setStatus("SENT");
        notification.setSentAt(Instant.now());
        notification.setCreatedAt(Instant.now());

        notificationRepository.save(notification);
        log.warn("Failure alert sent to {}: {}", recipient, message);
    }
}
