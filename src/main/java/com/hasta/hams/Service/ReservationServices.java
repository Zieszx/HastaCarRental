package com.hasta.hams.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.hasta.hams.model.Customer;
import com.hasta.hams.model.Payment;
import com.hasta.hams.model.Reservation;
import com.hasta.hams.model.Vehicle;
import com.hasta.hams.repository.PaymentRepository;
import com.hasta.hams.repository.ReservationRepository;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

@Service
public class ReservationServices {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        reservationRepository.findAll().forEach(reservations::add);
        return reservations;
    }

    public Reservation getReservation(int reservationId) {
        return reservationRepository.findById(reservationId).orElse(null);
    }

    public void addReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public void updateReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public void deleteReservation(int reservationId) {
        reservationRepository.deleteById(reservationId);
    }

    public void updateReservationStatus(Reservation reservation)
            throws IOException {
        reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationByCustomer(Customer customer) {
        return reservationRepository.findByCustomerID(customer);

    }

    public List<Reservation> getReservationByVehicle(Vehicle vehicle) {
        return reservationRepository.findByVehicleID(vehicle);
    }
}
