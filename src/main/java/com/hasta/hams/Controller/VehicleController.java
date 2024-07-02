package com.hasta.hams.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import com.hasta.hams.model.Maintenance;
import com.hasta.hams.model.Reservation;
import com.hasta.hams.model.Vehicle;
import com.hasta.hams.service.MaintenanceServices;
import com.hasta.hams.service.ReservationServices;
import com.hasta.hams.service.VehicleServices;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.support.StandardServletMultipartResolver;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
@NoArgsConstructor(force = true)
public class VehicleController {

    private VehicleServices vehicleServices;
    private ReservationServices reservationServices;
    private MaintenanceServices maintenanceServices;
    private ImageController imageController;
    private NotificationController notificationController;

    @GetMapping("/management/vehicles")
    public String vehicles(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        List<Vehicle> vehicles = vehicleServices.getAllVehicles();
        model.addAttribute("vehicles", vehicles);
        return "Management/MainVehicle";
    }

    @GetMapping("/management/addVehicle")
    public String addVehicle(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        model.addAttribute("vehicle", new Vehicle());
        return "Management/AddVehicle";
    }

    @PostMapping("/management/addVehicle")
    public String addVehicle(@ModelAttribute Vehicle vehicle, @RequestParam("imageFile") MultipartFile imageFile,
            Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        vehicle.setVehicleImage(imageController.setimageinDB(imageFile));
        vehicleServices.addVehicle(vehicle);

        // Create a notification message for the new vehicle
        String notificationMessage = "New vehicle added: " + vehicle.getVehicleBrand() + " " + vehicle.getVehicleModel()
                + " " + vehicle.getVehicleLicensePlate();
        String notificationTitle = "Vehicle Registration";
        String notificationType = "Vehicle";

        // Create the notification
        notificationController.createNotification(notificationType, notificationMessage, notificationTitle);

        return "redirect:/management/vehicles";
    }

    @GetMapping("/management/updateVehicle")
    public String editVehicle(@RequestParam("vehicleID") int id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        Vehicle vehicle = vehicleServices.getVehicle(id);
        model.addAttribute("vehicle", vehicle);
        return "Management/UpdateVehicle";
    }

    @PostMapping("/management/editVehicle")
    public String editVehicle(@ModelAttribute Vehicle vehicle, @RequestParam("imageFile") MultipartFile imageFile,
            Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        if (imageFile != null && !imageFile.isEmpty() && StringUtils.hasText(imageFile.getOriginalFilename())) {
            vehicle.setVehicleImage(imageController.setimageinDB(imageFile));
        } else {
            Vehicle temp = vehicleServices.getVehicle(vehicle.getVehicleID());
            vehicle.setVehicleImage(temp.getVehicleImage());
        }
        vehicleServices.updateVehicle(vehicle);

        // Create a notification message for the updated vehicle
        String notificationMessage = "Vehicle " + vehicle.getVehicleBrand() + " " + vehicle.getVehicleModel() + " "
                + vehicle.getVehicleLicensePlate() + " has been updated";
        String notificationTitle = "Vehicle Update";
        String notificationType = "Vehicle";

        // Create the notification
        notificationController.createNotification(notificationType, notificationMessage, notificationTitle);

        return "redirect:/management/vehicles";
    }

    @GetMapping("/management/deleteVehicle")
    public String deleteVehicle(@RequestParam("vehicleID") int id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        Vehicle vehicle = vehicleServices.getVehicle(id);
        List<Reservation> reservations = reservationServices.getReservationByVehicle(vehicle);
        List<Maintenance> maintenances = maintenanceServices.getMaintenanceByVehicle(vehicle);

        for (Reservation reservation : reservations) {
            if (reservation.getReservationStatus().equals("Booked")
                    || reservation.getReservationStatus().equals("Confirmed")) {
                model.addAttribute("message", "Vehicle is currently booked or confirmed. Cannot delete.");
                return "redirect:/management/vehicles";
            }
        }

        for (Maintenance maintenance : maintenances) {
            if (maintenance.getMaintenanceStatus().equals("Submitted")
                    || maintenance.getMaintenanceStatus().equals("In Progress")) {
                model.addAttribute("message", "Vehicle is currently in maintenance. Cannot delete.");
                return "redirect:/management/vehicles";
            }
        }

        vehicleServices.deleteVehicle(id);

        return "redirect:/management/vehicles";
    }

}
