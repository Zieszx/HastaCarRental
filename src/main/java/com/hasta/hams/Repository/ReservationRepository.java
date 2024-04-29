package com.hasta.hams.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hasta.hams.Model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

}
