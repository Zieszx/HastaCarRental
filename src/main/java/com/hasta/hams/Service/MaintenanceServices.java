package com.hasta.hams.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hasta.hams.model.Maintenance;
import com.hasta.hams.model.Vehicle;
import com.hasta.hams.repository.MaintenanceRepository;

import java.util.List;
import java.util.ArrayList;

@Service
public class MaintenanceServices {

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    public List<Maintenance> getAllMaintenance() {
        List<Maintenance> maintenances = new ArrayList<>();
        maintenanceRepository.findAll().forEach(maintenances::add);
        return maintenances;
    }

    public Maintenance getMaintenance(int maintenanceId) {
        return maintenanceRepository.findById(maintenanceId).orElse(null);
    }

    public void addMaintenance(Maintenance maintenance) {
        maintenanceRepository.save(maintenance);
    }

    public void updateMaintenance(Maintenance maintenance) {
        maintenanceRepository.save(maintenance);
    }

    public void deleteMaintenance(int maintenanceId) {
        maintenanceRepository.deleteById(maintenanceId);
    }

    public List<Maintenance> getMaintenanceByVehicle(Vehicle vehicle) {
        return maintenanceRepository.findByVehicleID(vehicle);
    }

}
