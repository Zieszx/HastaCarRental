package com.hasta.hams.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hasta.hams.model.Notification;
import com.hasta.hams.repository.NotificationRepository;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

@Service
public class NotificationServices {

    @Autowired
    private NotificationRepository notificationRepository;

    public List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<>();
        notificationRepository.findAll().forEach(notifications::add);
        return notifications;
    }

    public Notification getNotification(int notificationId) {
        return notificationRepository.findById(notificationId).orElse(null);
    }

    public void addNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    public void updateNotification(Notification notification) {
        notificationRepository.save(notification);
    }

    public void deleteNotification(int notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public void updateNotificationStatus(Notification notification)
            throws IOException {
        notificationRepository.save(notification);
    }

}
