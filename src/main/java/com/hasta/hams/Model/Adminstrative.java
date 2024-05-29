package com.hasta.hams.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Adminstrative")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Adminstrative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adminID")
    private int adminID;

    @Column(name = "adminName")
    private String adminName;

    @Column(name = "adminUsername")
    private String adminUsername;

    @Column(name = "adminPassword")
    private String adminPassword;

    @Column(name = "adminEmail")
    private String adminEmail;

}
