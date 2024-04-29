package com.hasta.hams.Model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity
@Table(name = "Reservation")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Reservation_ID")
    private int Reservation_ID;

    @OneToOne
    @JoinColumn(name = "Customer_ID")
    private Customer Customer_ID;

    @OneToOne
    @JoinColumn(name = "Vehicle_ID")
    private Vehicle Vehicle_ID;

    @OneToOne
    @JoinColumn(name = "Payment_ID")
    private Payment Payment_ID;

    @Column(name = "Reservation_PickupLocation")
    private String Reservation_PickupLocation;

    @Column(name = "Reservation_ReturnLocation")
    private String Reservation_ReturnLocation;

    @Column(name = "Reservation_StartDate")
    private Date Reservation_StartDate;

    @Column(name = "Reservation_EndDate")
    private Date Reservation_EndDate;

    // Time in 24 hour format
    @Column(name = "Reservation_Time")
    private String Reservation_Time;

    @Column(name = "Reservation_Hours")
    private int Reservation_Hours;

    // Booked, Confirm, Return, Refund, Cancel
    @Column(name = "Reservation_Status")
    private String Reservation_Status;

    @Column(name = "Reservation_ReasonDeleted", columnDefinition = "LONGTEXT", nullable = true)
    private String Reservation_ReasonDeleted;

}
