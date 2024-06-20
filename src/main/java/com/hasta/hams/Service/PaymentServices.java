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

    public void savePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    public List<Payment> getAllPayment() {
        List<Payment> payments = new ArrayList<>();
        paymentRepository.findAll().forEach(payments::add);
        return payments;
    }

    public Payment getPayment(int paymentid) {
        return paymentRepository.findById(paymentid).orElse(null);
    }

    public void addPayment(Payment payment) {
        paymentRepository.save(payment);
    }

    public void updatePayment(Payment payment) {
        paymentRepository.save(payment);
    }

    public void deletePayment(int paymentId) {
        paymentRepository.deleteById(paymentId);
    }

}
