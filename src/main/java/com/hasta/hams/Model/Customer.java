package com.hasta.hams.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Customer")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custID")
    private int custID;

    @Column(name = "custName")
    private String custName;

    @Column(name = "custPhone")
    private String custPhone;

    @Column(name = "custEmail")
    private String custEmail;

    @Column(name = "custDLicense")
    private String custDLicense;

    // Outsider, Student
    @Column(name = "custRole")
    private String custRole;

    @Column(name = "custAddress")
    private String custAddress;
}
