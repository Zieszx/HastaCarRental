package com.hasta.hams.Controller;

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

import com.hasta.hams.Model.*;
import com.hasta.hams.Service.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
@NoArgsConstructor(force = true)
@RequestMapping("/reservation")
public class ReservationController {

    private final VehicleServices vehicleService;
    private final CustomerServices customerService;
    private final ReservationServices reservationService;
    private final PaymentServices paymentService;
    private final StaffServices staffServices;

    @GetMapping("/viewCars")
    public String viewCars(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        List<Vehicle> vehicles = vehicleService.getAllVehicle();
        model.addAttribute("vehicles", vehicles);
        return "Reservation/ViewCars";
    }

    // Api to get all cars json data except the image
    @GetMapping("/getAllCars")
    public ResponseEntity<List<Vehicle>> getAllCars(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "sort", required = false) String sort) {

        List<Vehicle> vehicles = vehicleService.getAllVehicle();
        List<Vehicle> filteredVehicles = vehicles.stream()
                .filter(vehicle -> (search == null
                        || vehicle.getVehicle_Model().toLowerCase().contains(search.toLowerCase())) &&
                        (status == null || status.isEmpty() || vehicle.getVehicle_Status().equalsIgnoreCase(status)) &&
                        (type == null || type.isEmpty() || vehicle.getVehicle_Type().equalsIgnoreCase(type)))
                .collect(Collectors.toList());

        if ("asc".equalsIgnoreCase(sort)) {
            filteredVehicles.sort(Comparator.comparingDouble(Vehicle::getReserved_perHours));
        } else if ("desc".equalsIgnoreCase(sort)) {
            filteredVehicles.sort(Comparator.comparingDouble(Vehicle::getReserved_perHours).reversed());
        }

        for (Vehicle vehicle : filteredVehicles) {
            vehicle.setVehicle_Image(null); // Exclude the image from the response
        }

        return ResponseEntity.ok().body(filteredVehicles);
    }

}
