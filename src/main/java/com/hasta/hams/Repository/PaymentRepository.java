package com.hasta.hams.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hasta.hams.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

}
