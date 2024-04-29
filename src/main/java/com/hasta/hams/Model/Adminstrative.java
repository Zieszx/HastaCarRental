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
    @Column(name = "Admin_ID")
    private int Admin_ID;

    @Column(name = "Admin_Name")
    private String Admin_Name;

    @Column(name = "Admin_Username")
    private String Admin_Username;

    @Column(name = "Admin_Password")
    private String Admin_Password;

    @Column(name = "Admin_Email")
    private String Admin_Email;

}
