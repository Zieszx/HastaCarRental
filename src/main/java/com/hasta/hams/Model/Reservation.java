package com.hasta.hams.Model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.time.LocalDate;

@Entity
@Table(name = "Reservation")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservationID")
    private int reservationID;

    @OneToOne
    @JoinColumn(name = "customerID")
    private Customer customerID;

    @OneToOne
    @JoinColumn(name = "vehicleID")
    private Vehicle vehicleID;

    @OneToOne
    @JoinColumn(name = "paymentID")
    private Payment paymentID;

    @Column(name = "reservationPickupLocation")
    private String reservationPickupLocation;

    @Column(name = "reservationReturnLocation")
    private String reservationReturnLocation;

    @Column(name = "reservationStartDate")
    private Date reservationStartDate;

    @Column(name = "reservationEndDate")
    private Date reservationEndDate;

    // Time in 24 hour format
    @Column(name = "reservationTime")
    private String reservationTime;

    @Column(name = "reservationHours")
    private int reservationHours;

    // Booked, Confirm, Return, Refund, Cancel
    @Column(name = "reservationStatus")
    private String reservationStatus;

    @Column(name = "reservationReasonDeleted", columnDefinition = "LONGTEXT", nullable = true)
    private String reservationReasonDeleted;

}
