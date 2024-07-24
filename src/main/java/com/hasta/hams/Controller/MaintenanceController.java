package com.hasta.hams.controller;

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

import com.hasta.hams.model.*;
import com.hasta.hams.service.*;

import java.sql.Date;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.time.format.DateTimeFormatter;
import java.io.IOException;

/**
 * The MaintenanceController class handles the HTTP requests related to vehicle
 * maintenance.
 * It provides methods for creating, updating, and deleting maintenance
 * requests, as well as
 * viewing all maintenance requests and managing them.
 */
@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
@NoArgsConstructor(force = true)
@RequestMapping("/maintenance")
public class MaintenanceController {

    private final VehicleServices vehicleServices;
    private final MaintenanceServices maintenanceServices;
    private NotificationController notificationController;

    /**
     * Displays the view for creating a maintenance request for a vehicle.
     * Retrieves all vehicles and adds them to the model.
     * 
     * @param model the model object to add attributes to
     * @return the name of the view template
     */
    @GetMapping("/viewCartoMaintenance")
    public String createMaintenance(Model model) {
        model.addAttribute("vehicles", vehicleServices.getAllVehicles());
        return "Maintenance/viewCartoMaintenance";
    }

    /**
     * Displays the view for creating a maintenance request for a specific vehicle.
     * Retrieves the vehicle with the given ID and adds it to the model.
     * 
     * @param model     the model object to add attributes to
     * @param vehicleID the ID of the vehicle
     * @return the name of the view template
     */
    @GetMapping("/createMaintenance")
    public String createMaintenance(Model model, @RequestParam("vehicleID") int vehicleID) {
        Vehicle vehicle = vehicleServices.getVehicle(vehicleID);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("maintenance", new Maintenance());

        return "Maintenance/createMaintenance";
    }

    /**
     * Handles the submission of a maintenance request for a vehicle.
     * Updates the vehicle status to "Maintenance" and adds the maintenance request.
     * Creates a notification for the maintenance request.
     * 
     * @param maintenance        the maintenance request object
     * @param vehicleID          the ID of the vehicle
     * @param redirectAttributes the redirect attributes object to add flash
     *                           attributes to
     * @return the redirect URL
     */
    @PostMapping("/createMaintenance")
    public String createMaintenance(@ModelAttribute("maintenance") Maintenance maintenance,
            @RequestParam("vehicleID") int vehicleID,
            RedirectAttributes redirectAttributes) {

        Vehicle vehicle = vehicleServices.getVehicle(vehicleID);
        vehicle.setVehicleStatus("Maintenance");
        vehicleServices.updateVehicle(vehicle);

        maintenance.setVehicleID(vehicle);
        maintenance.setMaintenanceStatus("Submitted");
        maintenanceServices.addMaintenance(maintenance);
        redirectAttributes.addFlashAttribute("message", "Maintenance created successfully!");

        // Create notification message
        String notificationMessage = "Maintenance request for " + vehicle.getVehicleLicensePlate() + " "
                + vehicle.getVehicleBrand() + " " + vehicle.getVehicleModel();
        String notificationTitle = "Vehicle Maintenance Request";
        String notificationType = "Maintenance";

        // Create the notification
        notificationController.createNotification(notificationType, notificationMessage, notificationTitle);

        return "redirect:/maintenance/viewCartoMaintenance";
    }

    /**
     * Displays the view for viewing all maintenance requests.
     * Retrieves all maintenance requests and adds them to the model.
     * 
     * @param model the model object to add attributes to
     * @return the name of the view template
     */
    @GetMapping("viewAllMaintenance")
    public String viewAllMaintenance(Model model) {
        model.addAttribute("maintenances", maintenanceServices.getAllMaintenance());
        return "Maintenance/viewAllMaintenance";
    }

    /**
     * Displays the view for managing maintenance requests.
     * Retrieves all maintenance requests and adds them to the model.
     * 
     * @param model the model object to add attributes to
     * @return the name of the view template
     */
    @GetMapping("/manageMaintenance")
    public String manageMaintenance(Model model) {
        model.addAttribute("maintenances", maintenanceServices.getAllMaintenance());
        return "Maintenance/manageMaintenance";
    }

    /**
     * Displays the view for updating a maintenance request.
     * Retrieves the maintenance request with the given ID and adds it to the model.
     * Retrieves the associated vehicle and adds it to the model.
     * 
     * @param maintenanceID the ID of the maintenance request
     * @param model         the model object to add attributes to
     * @return the name of the view template
     */
    @GetMapping("/updateMaintenance")
    public String updateMaintenance(@RequestParam("maintenanceID") int maintenanceID, Model model) {
        Maintenance maintenance = maintenanceServices.getMaintenance(maintenanceID);
        model.addAttribute("maintenance", maintenance);
        Vehicle vehicle = vehicleServices.getVehicle(maintenance.getVehicleID().getVehicleID());
        model.addAttribute("vehicle", vehicle);
        return "Maintenance/updateMaintenance";
    }

    /**
     * Handles the submission of an updated maintenance request.
     * Updates the maintenance request and the associated vehicle.
     * Creates a notification for the updated maintenance request.
     * 
     * @param maintenance        the updated maintenance request object
     * @param redirectAttributes the redirect attributes object to add flash
     *                           attributes to
     * @return the redirect URL
     */
    @PostMapping("/updateMaintenance")
    public String updateMaintenance(@ModelAttribute("maintenance") Maintenance maintenance,
            RedirectAttributes redirectAttributes) {
        maintenanceServices.updateMaintenance(maintenance);
        if (maintenance.getMaintenanceStatus().equalsIgnoreCase("Complete")) {
            Vehicle vehicle = vehicleServices.getVehicle(maintenance.getVehicleID().getVehicleID());
            vehicle.setVehicleStatus("Available");
            vehicle.setVehicleMileage(maintenance.getMaintenanceMileeageDuring());
            vehicleServices.updateVehicle(vehicle);
        } else {
            Vehicle vehicle = vehicleServices.getVehicle(maintenance.getVehicleID().getVehicleID());
            vehicle.setVehicleStatus("Maintenance");
            vehicleServices.updateVehicle(vehicle);
        }
        redirectAttributes.addFlashAttribute("message", "Maintenance updated successfully!");

        // Create notification message
        String notificationMessage = "Maintenance request for " + maintenance.getVehicleID().getVehicleLicensePlate()
                + " " + maintenance.getVehicleID().getVehicleBrand() + " "
                + maintenance.getVehicleID().getVehicleModel() + " has been updated";
        String notificationTitle = "Vehicle Maintenance Request";
        String notificationType = "Maintenance";

        // Create the notification
        notificationController.createNotification(notificationType, notificationMessage, notificationTitle);

        return "redirect:/maintenance/manageMaintenance";
    }

    /**
     * Deletes a maintenance request with the given ID.
     * Sets the associated vehicle status to "Available".
     * 
     * @param maintenanceID      the ID of the maintenance request
     * @param redirectAttributes the redirect attributes object to add flash
     *                           attributes to
     * @return the redirect URL
     */
    @GetMapping("/deleteMaintenance")
    public String deleteMaintenance(@RequestParam("maintenanceID") int maintenanceID,
            RedirectAttributes redirectAttributes) {
        vehicleServices.getVehicle(maintenanceID).setVehicleStatus("Available");
        maintenanceServices.deleteMaintenance(maintenanceID);
        redirectAttributes.addFlashAttribute("message", "Maintenance deleted successfully!");
        return "redirect:/maintenance/manageMaintenance";
    }
}
