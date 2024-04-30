package com.hasta.hams.Model;

import org.springframework.web.multipart.MultipartFile;

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
    @Column(name = "Vehicle_ID")
    private int Vehicle_ID;

    @Column(name = "Vehicle_Fuelbar")
    private int Vehicle_Fuelbar;

    @Column(name = "Vehicle_Mileage")
    private int Vehicle_Mileage;

    @Column(name = "Vehicle_Model")
    private String Vehicle_Model;

    @Column(name = "Vehicle_Brand")
    private String Vehicle_Brand;

    // Sedan, MPV, Compact
    @Column(name = "Vehicle_Type")
    private String Vehicle_Type;

    @Column(name = "Vehicle_Year")
    private int Vehicle_Year;

    @Column(name = "Vehicle_LicensePlate")
    private String Vehicle_LicensePlate;

    // Available, Maintenance, Unavailable
    @Column(name = "Vehicle_Status")
    private String Vehicle_Status;

    @Column(name = "Reserved_perHours")
    private double Reserved_perHours;

    @Lob
    @Column(name = "Vehicle_Image")
    private byte[] Vehicle_Image;

    @Transient
    private MultipartFile Vehicle_ImageFile;

}
