package com.hasta.hams.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;

@Entity
@Table(name = "Notification")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notificationID")
    private int notificationID;

    @Column(name = "notificationTitle")
    private String notificationTitle;

    @Column(name = "notificationMessage", columnDefinition = "LONGTEXT")
    private String notificationMessage;

    @Column(name = "notificationURL")
    private String notificationURL;

    // Vehicle, Maintenance, Customer, Reservation
    @Column(name = "notificationType")
    private String notificationType;

    @Column(name = "notificationDate")
    private Date notificationDate;

    @Column(name = "notificationTime")
    private Time notificationTime;

    // Unread, Read
    @Column(name = "notificationStatus")
    private String notificationStatus;

}
