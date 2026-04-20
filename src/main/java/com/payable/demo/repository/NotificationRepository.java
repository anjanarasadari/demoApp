package com.payable.demo.repository;

import com.payable.demo.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationLog, Long> {
    Optional<NotificationLog> findByNotificationRef(String notificationRef);
}
