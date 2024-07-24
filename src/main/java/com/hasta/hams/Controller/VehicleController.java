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
import java.util.HashMap;
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

    /**
     * Retrieves all vehicles and renders the Management/MainVehicle view.
     * 
     * @param model   the model object to add attributes to
     * @param session the HttpSession object to check user authentication
     * @return the view name to render
     */
    @GetMapping("/management/vehicles")
    public String vehicles(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        List<Vehicle> vehicles = vehicleServices.getAllVehicles();
        model.addAttribute("vehicles", vehicles);
        return "Management/MainVehicle";
    }

    /**
     * Renders the Management/AddVehicle view to add a new vehicle.
     * 
     * @param model   the model object to add attributes to
     * @param session the HttpSession object to check user authentication
     * @return the view name to render
     */
    @GetMapping("/management/addVehicle")
    public String addVehicle(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        model.addAttribute("vehicle", new Vehicle());
        return "Management/AddVehicle";
    }

    /**
     * Adds a new vehicle to the database.
     * 
     * @param vehicle   the Vehicle object to add
     * @param imageFile the image file of the vehicle
     * @param model     the model object to add attributes to
     * @param session   the HttpSession object to check user authentication
     * @return the view name to render
     */
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

    /**
     * Renders the Management/UpdateVehicle view to edit a vehicle.
     * 
     * @param id      the ID of the vehicle to edit
     * @param model   the model object to add attributes to
     * @param session the HttpSession object to check user authentication
     * @return the view name to render
     */
    @GetMapping("/management/updateVehicle")
    public String editVehicle(@RequestParam("vehicleID") int id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        Vehicle vehicle = vehicleServices.getVehicle(id);
        model.addAttribute("vehicle", vehicle);
        return "Management/UpdateVehicle";
    }

    /**
     * Updates a vehicle in the database.
     * 
     * @param vehicle   the updated Vehicle object
     * @param imageFile the updated image file of the vehicle
     * @param model     the model object to add attributes to
     * @param session   the HttpSession object to check user authentication
     * @return the view name to render
     */
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

    /**
     * Deletes a vehicle from the database.
     * 
     * @param id      the ID of the vehicle to delete
     * @param session the HttpSession object to check user authentication
     * @return a ResponseEntity object with the status and message of the deletion
     */
    @GetMapping("/management/deleteVehicle")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteVehicle(@RequestParam("vehicleID") int id, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        if (session.getAttribute("user") == null) {
            response.put("status", "error");
            response.put("message", "User not logged in.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Vehicle vehicle = vehicleServices.getVehicle(id);
        List<Reservation> reservations = reservationServices.getReservationByVehicle(vehicle);
        List<Maintenance> maintenances = maintenanceServices.getMaintenanceByVehicle(vehicle);

        for (Reservation reservation : reservations) {
            if (reservation.getReservationStatus().equals("Booked")
                    || reservation.getReservationStatus().equals("Confirmed")) {
                response.put("status", "error");
                response.put("message", "Vehicle is currently booked or confirmed. Cannot delete.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }

        for (Maintenance maintenance : maintenances) {
            if (maintenance.getMaintenanceStatus().equals("Submitted")
                    || maintenance.getMaintenanceStatus().equals("In Progress")) {
                response.put("status", "error");
                response.put("message", "Vehicle is currently in maintenance. Cannot delete.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }

        vehicleServices.deleteVehicle(id);
        response.put("status", "success");
        response.put("message", "Vehicle deleted successfully.");
        return ResponseEntity.ok(response);
    }

}
