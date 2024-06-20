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
    @Column(name = "maintenanceID")
    private int maintenanceID;

    @ManyToOne
    @JoinColumn(name = "vehicleID")
    private Vehicle vehicleID;

    @Column(name = "maintenanceDesc", columnDefinition = "LONGTEXT")
    private String maintenanceDesc;

    @Column(name = "maintenanceRejectReason", columnDefinition = "LONGTEXT", nullable = true)
    private String maintenanceRejectReason;

    @Column(name = "maintenanceMileeageDuring")
    private int maintenanceMileeageDuring;

    @Column(name = "maintenanceDate")
    private Date maintenanceDate;

    // Submitted, In Progress, Complete, Rejected, Cancel
    @Column(name = "maintenanceStatus")
    private String maintenanceStatus;
}
