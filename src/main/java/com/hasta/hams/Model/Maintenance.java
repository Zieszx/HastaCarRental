package com.hasta.hams.Model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity
@Table(name = "Maintenance")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Maintenance_ID")
    private int Maintenance_ID;

    @OneToOne
    @JoinColumn(name = "Vehicle_ID")
    private Vehicle Vehicle_ID;

    @Column(name = "Maintenance_Desc", columnDefinition = "LONGTEXT")
    private String Maintenance_Desc;

    @Column(name = "Maintenance_RejectReason", columnDefinition = "LONGTEXT", nullable = true)
    private String Maintenance_RejectReason;

    @Column(name = "Maintenance_MileeageDuring")
    private int Maintenance_MileeageDuring;

    @Column(name = "Maintenance_Date")
    private Date Maintenance_Date;

    // Submitted, In Progress, Complete, Rejected, Cancel
    @Column(name = "Maintenance_Status")
    private String Maintenance_Status;
}
