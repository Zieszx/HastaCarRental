package com.hasta.hams.Model;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Vehicle")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicleID")
    private int vehicleID;

    @Column(name = "vehicleFuelbar")
    private int vehicleFuelbar;

    @Column(name = "vehicleMileage")
    private int vehicleMileage;

    @Column(name = "vehicleModel")
    private String vehicleModel;

    @Column(name = "vehicleBrand")
    private String vehicleBrand;

    // Sedan, MPV, Compact, SUV
    @Column(name = "vehicleType")
    private String vehicleType;

    @Column(name = "vehicleYear")
    private int vehicleYear;

    @Column(name = "vehicleLicensePlate")
    private String vehicleLicensePlate;

    // Available, Maintenance, Unavailable
    @Column(name = "vehicleStatus")
    private String vehicleStatus;

    @Column(name = "vehicleReservedperHours")
    private double vehicleReservedperHours;

    @Lob
    @Column(name = "vehicleImage", columnDefinition = "MEDIUMBLOB")
    @JsonIgnore
    private byte[] vehicleImage;

    @Transient
    private MultipartFile vehicleImageFile;

}
