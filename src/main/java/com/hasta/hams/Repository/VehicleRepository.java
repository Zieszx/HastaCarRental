package com.hasta.hams.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hasta.hams.Model.Vehicle;

public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {

}
