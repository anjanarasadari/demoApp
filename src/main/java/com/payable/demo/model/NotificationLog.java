package com.payable.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "notification_logs", indexes = {
        @Index(name = "idx_notification_logs_user_id",
                columnList = "user_id"),
        @Index(name = "idx_notification_logs_order_id",
                columnList = "order_id"),
        @Index(name = "idx_notification_logs_status_created",
                columnList = "status, created_at"),
        @Index(name = "idx_notification_logs_created_at",
                columnList = "created_at")}, uniqueConstraints = {@UniqueConstraint(name = "uk_notification_logs_ref",
        columnNames = {"notification_ref"})})
public class NotificationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "notification_ref", nullable = false, length = 36)
    private String notificationRef;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "payment_transaction_id")
    private PaymentTransaction paymentTransaction;

    @Column(name = "channel", nullable = false, length = 30)
    private String channel;

    @Column(name = "notification_type", nullable = false, length = 50)
    private String notificationType;

    @Column(name = "recipient", nullable = false)
    private String recipient;

    @Column(name = "subject")
    private String subject;

    @Lob
    @Column(name = "message", nullable = false)
    private String message;

    @ColumnDefault("'PENDING'")
    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "failure_reason", length = 500)
    private String failureReason;

    @Column(name = "sent_at")
    private Instant sentAt;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;


}