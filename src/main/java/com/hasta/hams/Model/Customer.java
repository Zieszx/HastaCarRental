package com.hasta.hams.Model;

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
    @Column(name = "Cust_ID")
    private int Cust_ID;

    @Column(name = "Cust_Name")
    private String Cust_Name;

    @Column(name = "Cust_Phone")
    private String Cust_Phone;

    @Column(name = "Cust_Email")
    private String Cust_Email;

    @Column(name = "Cust_DLicense")
    private String Cust_DLicense;

    // Outsider, Student
    @Column(name = "Cust_Role")
    private String Cust_Role;

    @Column(name = "Cust_Address")
    private String Cust_Address;
}
