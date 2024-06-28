package com.hasta.hams.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hasta.hams.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

}
