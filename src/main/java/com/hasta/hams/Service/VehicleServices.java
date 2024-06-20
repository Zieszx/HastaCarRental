package com.hasta.hams.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hasta.hams.Model.Vehicle;
import com.hasta.hams.Repository.VehicleRepository;

import java.util.List;
import java.util.ArrayList;

@Service
public class VehicleServices {

    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        vehicleRepository.findAll().forEach(vehicles::add);
        return vehicles;
    }

    public Vehicle getVehicle(int vehicleId) {
        return vehicleRepository.findById(vehicleId).orElse(null);
    }

    public void addVehicle(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
    }

    public void updateVehicle(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(int vehicleId) {
        vehicleRepository.deleteById(vehicleId);
    }

}
