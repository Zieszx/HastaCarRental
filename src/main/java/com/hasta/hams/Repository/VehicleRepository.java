package com.hasta.hams.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hasta.hams.model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

}
