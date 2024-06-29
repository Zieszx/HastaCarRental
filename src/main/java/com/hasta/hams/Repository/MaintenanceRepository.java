package com.hasta.hams.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hasta.hams.model.Maintenance;
import com.hasta.hams.model.Vehicle;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Integer> {

    List<Maintenance> findByVehicleID(Vehicle vehicleID);

}
