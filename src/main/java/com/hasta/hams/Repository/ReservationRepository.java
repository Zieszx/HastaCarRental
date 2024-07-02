package com.hasta.hams.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hasta.hams.model.Customer;
import com.hasta.hams.model.Reservation;
import com.hasta.hams.model.Vehicle;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    List<Reservation> findByCustomerID(Customer customerID);

    List<Reservation> findByVehicleID(Vehicle vehicleID);

}
