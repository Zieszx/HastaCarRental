package com.hasta.hams.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import com.hasta.hams.Model.*;
import com.hasta.hams.Service.*;

import java.sql.Date;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import java.io.IOException;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
@NoArgsConstructor(force = true)
@RequestMapping("/maintenance")
public class MaintenanceController {

    private final VehicleServices vehicleServices;
    private final MaintenanceServices maintenanceServices;
    private final StaffServices staffServices;

    @GetMapping("/viewCartoMaintenance")
    public String createMaintenance(Model model) {
        model.addAttribute("vehicles", vehicleServices.getAllVehicles());
        return "Maintenance/viewCartoMaintenance";
    }

    @GetMapping("/createMaintenance")
    public String createMaintenance(Model model, @RequestParam("vehicleID") int vehicleID) {
        Vehicle vehicle = vehicleServices.getVehicle(vehicleID);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("maintenance", new Maintenance());
        return "Maintenance/createMaintenance";
    }

    @PostMapping("/createMaintenance")
    public String createMaintenance(@ModelAttribute("maintenance") Maintenance maintenance,
            @RequestParam("vehicleID") int vehicleID, RedirectAttributes redirectAttributes) {

        Vehicle vehicle = vehicleServices.getVehicle(vehicleID);
        vehicle.setVehicleStatus("Maintenance");
        vehicleServices.updateVehicle(vehicle);

        maintenance.setVehicleID(vehicle);
        maintenance.setMaintenanceStatus("Submitted");

        maintenanceServices.addMaintenance(maintenance);
        redirectAttributes.addFlashAttribute("message", "Maintenance created successfully!");
        return "redirect:/maintenance/viewCartoMaintenance";
    }

    @GetMapping("viewAllMaintenance")
    public String viewAllMaintenance(Model model) {
        model.addAttribute("maintenances", maintenanceServices.getAllMaintenance());
        return "Maintenance/viewAllMaintenance";
    }

    @GetMapping("/manageMaintenance")
    public String manageMaintenance(Model model) {
        model.addAttribute("maintenances", maintenanceServices.getAllMaintenance());
        return "Maintenance/manageMaintenance";
    }

    @GetMapping("/updateMaintenance")
    public String updateMaintenance(@RequestParam("maintenanceID") int maintenanceID, Model model) {
        Maintenance maintenance = maintenanceServices.getMaintenance(maintenanceID);
        model.addAttribute("maintenance", maintenance);
        Vehicle vehicle = vehicleServices.getVehicle(maintenance.getVehicleID().getVehicleID());
        model.addAttribute("vehicle", vehicle);
        return "Maintenance/updateMaintenance";
    }

    @PostMapping("/updateMaintenance")
    public String updateMaintenance(@ModelAttribute("maintenance") Maintenance maintenance,
            RedirectAttributes redirectAttributes) {
        maintenanceServices.updateMaintenance(maintenance);
        if (maintenance.getMaintenanceStatus().equalsIgnoreCase("Complete")) {
            Vehicle vehicle = vehicleServices.getVehicle(maintenance.getVehicleID().getVehicleID());
            vehicle.setVehicleStatus("Available");
            vehicleServices.updateVehicle(vehicle);
        } else {
            Vehicle vehicle = vehicleServices.getVehicle(maintenance.getVehicleID().getVehicleID());
            vehicle.setVehicleStatus("Maintenance");
            vehicleServices.updateVehicle(vehicle);
        }
        redirectAttributes.addFlashAttribute("message", "Maintenance updated successfully!");
        return "redirect:/maintenance/manageMaintenance";
    }

    @GetMapping("/deleteMaintenance")
    public String deleteMaintenance(@RequestParam("maintenanceID") int maintenanceID,
            RedirectAttributes redirectAttributes) {
        maintenanceServices.deleteMaintenance(maintenanceID);
        redirectAttributes.addFlashAttribute("message", "Maintenance deleted successfully!");
        return "redirect:/maintenance/manageMaintenance";
    }
}
