package com.hasta.hams.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hasta.hams.Model.Maintenance;
import com.hasta.hams.Repository.MaintenanceRepository;

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

    public Maintenance getMaintenance(int id) {
        return maintenanceRepository.findById(id).get();
    }

    public void addMaintenance(Maintenance maintenance) {
        maintenanceRepository.save(maintenance);
    }

    public void updateMaintenance(Maintenance maintenance) {
        maintenanceRepository.save(maintenance);
    }

    public void deleteMaintenance(int id) {
        maintenanceRepository.deleteById(id);
    }

}
