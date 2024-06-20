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
    @Column(name = "paymentID")
    private int paymentId;

    @Column(name = "paymentAmount")
    private double paymentAmount;

    @Column(name = "paymentDeposit")
    private double paymentDeposit;

    @Column(name = "paymentAddtional")
    private double paymentAddtional;

    @Column(name = "paymentDescriptions", columnDefinition = "LONGTEXT")
    private String paymentDescriptions;

    @Column(name = "paymentDate")
    private String paymentDate;

    @Column(name = "paymentMethod", nullable = true)
    private String paymentMethod;

    // Created, Deposited, Full Payment, Successful, Cancel
    @Column(name = "paymentStatus")
    private String paymentStatus;

    @Lob
    @Column(name = "paymentDepositimage", columnDefinition = "MEDIUMBLOB", nullable = true)
    private byte[] paymentDepositimage;

    @Lob
    @Column(name = "paymentFullimage", columnDefinition = "MEDIUMBLOB", nullable = true)
    private byte[] paymentFullimage;

    @Lob
    @Column(name = "paymentAdditionalimage", columnDefinition = "MEDIUMBLOB", nullable = true)
    private byte[] paymentAdditionalimage;

}
