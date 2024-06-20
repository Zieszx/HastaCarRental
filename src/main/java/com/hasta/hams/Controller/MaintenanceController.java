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

    @GetMapping("/Maintenancejson")
    public ResponseEntity<List<Maintenance>> getAllMaintenance() {
        List<Maintenance> maintenances = maintenanceServices.getAllMaintenance();
        return ResponseEntity.ok().body(maintenances);
    }

    @GetMapping("/getAllCarstoMaintenance")
    public ResponseEntity<List<Vehicle>> getAllCars(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type) {

        List<Vehicle> vehicles = vehicleServices.getAllVehicles();

        // Filter vehicles based on search, status, and type
        List<Vehicle> filteredVehicles = vehicles.stream()
                .filter(vehicle -> (search == null
                        || vehicle.getVehicleModel().toLowerCase().contains(search.toLowerCase())) &&
                        (status == null || status.isEmpty() || vehicle.getVehicleStatus().equalsIgnoreCase(status)) &&
                        (type == null || type.isEmpty() || vehicle.getVehicleType().equalsIgnoreCase(type)))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(filteredVehicles);
    }
}
