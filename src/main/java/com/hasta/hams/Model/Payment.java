package com.hasta.hams.Model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Payment")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Payment_ID")
    private int Payment_Id;

    @Column(name = "Payment_Amount")
    private double Payment_Amount;

    @Column(name = "Payment_Deposit")
    private double Payment_Deposit;

    @Column(name = "Payment_Addtional")
    private double Payment_Addtional;

    @Column(name = "Payment_Descriptions", columnDefinition = "LONGTEXT")
    private String Payment_Descriptions;

    @Column(name = "Payment_Date")
    private String payment_Date;

    @Column(name = "Payment_Method", nullable = true)
    private String payment_Method;

    // Created, Deposited, Full Payment, Successful, Cancel
    @Column(name = "Payment_Status")
    private String payment_Status;

    @Lob
    @Column(name = "Deposit_image", columnDefinition = "MEDIUMBLOB", nullable = true)
    private byte[] Deposit_image;

    @Lob
    @Column(name = "FullPayment_image", columnDefinition = "MEDIUMBLOB", nullable = true)
    private byte[] FullPayment_image;

    @Lob
    @Column(name = "Additional_image", columnDefinition = "MEDIUMBLOB", nullable = true)
    private byte[] Additional_image;

}
