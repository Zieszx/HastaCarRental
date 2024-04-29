package com.hasta.hams.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hasta.hams.Model.Payment;
import com.hasta.hams.Repository.PaymentRepository;

import java.util.List;
import java.util.ArrayList;

@Service
public class PaymentServices {

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Payment> getAllPayment() {
        List<Payment> payments = new ArrayList<>();
        paymentRepository.findAll().forEach(payments::add);
        return payments;
    }

    public Payment getPayment(int id) {
        return paymentRepository.findById(id).get();
    }

    public void addPayment(Payment payment) {
        paymentRepository.save(payment);
    }

    public void updatePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    public void deletePayment(int id) {
        paymentRepository.deleteById(id);
    }

}
