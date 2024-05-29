package com.hasta.hams.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Staff")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "staffID")
    private int staffID;

    @Column(name = "staffName")
    private String staffName;

    @Column(name = "staffUsername")
    private String staffUsername;

    @Column(name = "staffPassword")
    private String staffPassword;

    @Column(name = "staffEmail")
    private String staffEmail;

    @Column(name = "staffPhone")
    private String staffPhone;

}
