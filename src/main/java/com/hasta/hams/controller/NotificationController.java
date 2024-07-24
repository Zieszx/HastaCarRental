package com.hasta.hams.controller;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.hasta.hams.model.Notification;
import com.hasta.hams.service.NotificationServices;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class NotificationController {

    private NotificationServices notificationServices;

    /**
     * Creates a new notification with the specified type, message, and title.
     *
     * @param notificationType    The type of the notification.
     * @param notificationMessage The message of the notification.
     * @param notificationTitle   The title of the notification.
     */
    public void createNotification(String notificationType, String notificationMessage, String notificationTitle) {
        Notification notification = new Notification();

        notification.setNotificationMessage(notificationMessage);
        notification.setNotificationTitle(notificationTitle);
        notification.setNotificationType(notificationType);
        notification.setNotificationStatus("Unread");

        LocalDateTime now = LocalDateTime.now();
        notification.setNotificationDate(Date.valueOf(now.toLocalDate()));
        notification.setNotificationTime(Time.valueOf(now.toLocalTime()));

        // Set the notificationURL based on notificationType
        switch (notificationType) {
            case "Vehicle":
                notification.setNotificationURL("/management/vehicles");
                break;
            case "Maintenance":
                notification.setNotificationURL("/maintenance/manageMaintenance");
                break;
            case "Customer":
                notification.setNotificationURL("/management/customers");
                break;
            case "Reservation":
                notification.setNotificationURL("/reservation/manageReservation");
                break;
            default:
                notification.setNotificationURL("/");
                break;
        }

        // Save the notification
        notificationServices.addNotification(notification);

        // if the notification size it more than 30, delete the oldest notification
        List<Notification> notifications = notificationServices.getAllNotifications();
        if (notifications.size() > 1) {
            notificationServices.deleteNotification(notifications.get(0).getNotificationID());
        }

    }

    /**
     * Retrieves all notifications.
     *
     * @return A ResponseEntity containing a list of notifications and the HTTP
     *         status code.
     */
    @GetMapping("/api/notifications")
    public ResponseEntity<List<Notification>> getNotifications() {
        List<Notification> notifications = notificationServices.getAllNotifications();
        return new ResponseEntity<>(notifications, HttpStatus.OK);
    }

    /**
     * Marks a notification as read.
     *
     * @param id The ID of the notification to mark as read.
     * @return A ResponseEntity with the HTTP status code indicating the success or
     *         failure of the operation.
     */
    @PostMapping("/api/notifications/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable int id) {
        Notification notification = notificationServices.getNotification(id);
        if (notification != null) {
            notification.setNotificationStatus("Read");
            notificationServices.updateNotification(notification);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
