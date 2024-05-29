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
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import com.hasta.hams.Model.*;
import com.hasta.hams.Service.*;

import java.time.LocalDate;
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

    @GetMapping("/reservedCar")
    public String reservedCar(@RequestParam("vehicleID") int vehicleID, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        List<Customer> customers = customerService.getAllCustomer();
        model.addAttribute("customers", customers);

        Vehicle vehicle = vehicleService.getVehicle(vehicleID);
        model.addAttribute("vehicle", vehicle);

        model.addAttribute("reservation", new Reservation());
        return "Reservation/ReservedCar";
    }

    @PostMapping("/reservedCar")
    public String reservedCar(@ModelAttribute Reservation reservation, @RequestParam("vehicleID") int vehicleID,
            @RequestParam("customerID") int customerID, @RequestParam("paymentDeposit") double paymentDeposit,
            @RequestParam("imageFile") MultipartFile imageFile,
            BindingResult result, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        // Get the vehicle
        Vehicle vehicle = vehicleService.getVehicle(vehicleID);

        // Create the payment data
        Payment payment = new Payment();
        payment.setPaymentAmount(reservation.getReservationHours() * vehicle.getVehicleReservedperHours());
        payment.setPaymentDeposit(paymentDeposit);
        payment.setPaymentStatus("Deposited");
        payment.setPaymentDate(new java.util.Date().toString());

        // Save the payment data
        paymentService.savePayment(payment);

        // Set the payment ID to reservation
        reservation.setPaymentID(payment);

        // Set reservation status
        reservation.setReservationStatus("Booked");
        reservation.setVehicleID(vehicle);

        // Save the reservation data
        reservationService.addReservation(reservation);

        return "redirect:/reservation/viewCars";
    }

    // Api to get all cars json data except the image
    @GetMapping("/getAllCars")
    public ResponseEntity<List<Vehicle>> getAllCars(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Vehicle> vehicles = vehicleService.getAllVehicle();

        // Filter vehicles based on search, status, and type
        List<Vehicle> filteredVehicles = vehicles.stream()
                .filter(vehicle -> (search == null
                        || vehicle.getVehicleModel().toLowerCase().contains(search.toLowerCase())) &&
                        (status == null || status.isEmpty() || vehicle.getVehicleStatus().equalsIgnoreCase(status)) &&
                        (type == null || type.isEmpty() || vehicle.getVehicleType().equalsIgnoreCase(type)))
                .collect(Collectors.toList());

        // Filter vehicles based on date availability
        if (startDate != null && endDate != null) {
            List<Reservation> reservations = reservationService.getAllReservations();

            filteredVehicles = filteredVehicles.stream()
                    .filter(vehicle -> reservations.stream().noneMatch(reservation -> reservation.getVehicleID()
                            .equals(vehicle) &&
                            !(convertToLocalDate(reservation.getReservationEndDate()).isBefore(startDate)
                                    || convertToLocalDate(reservation.getReservationStartDate()).isAfter(endDate))))
                    .collect(Collectors.toList());
        }

        // Sort vehicles based on price
        if ("asc".equalsIgnoreCase(sort)) {
            filteredVehicles.sort(Comparator.comparingDouble(Vehicle::getVehicleReservedperHours));
        } else if ("desc".equalsIgnoreCase(sort)) {
            filteredVehicles.sort(Comparator.comparingDouble(Vehicle::getVehicleReservedperHours).reversed());
        }

        return ResponseEntity.ok().body(filteredVehicles);
    }

    private LocalDate convertToLocalDate(Date dateToConvert) {
        return ((java.sql.Date) dateToConvert).toLocalDate();
    }

}
