package com.hasta.hams.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hasta.hams.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

}
