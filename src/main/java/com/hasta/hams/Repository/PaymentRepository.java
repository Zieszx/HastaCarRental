package com.hasta.hams.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hasta.hams.Model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

}
