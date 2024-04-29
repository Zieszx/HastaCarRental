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
    @Column(name = "Staff_ID")
    private int Staff_ID;

    @Column(name = "Staff_Name")
    private String Staff_Name;

    @Column(name = "Staff_Username")
    private String Staff_Username;

    @Column(name = "Staff_Password")
    private String Staff_Password;

    @Column(name = "Staff_Email")
    private String Staff_Email;

    @Column(name = "Staff_Phone")
    private String Staff_Phone;

}
