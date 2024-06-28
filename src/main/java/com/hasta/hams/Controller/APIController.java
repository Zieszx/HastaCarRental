package com.hasta.hams.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

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

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
@NoArgsConstructor(force = true)
public class APIController {

    private final AdminstrativeServices adminstrativeServices;
    private final MaintenanceServices maintenanceServices;
    private final StaffServices staffServices;
    private final VehicleServices vehicleServices;
    private final ReservationServices reservationServices;
    private final CustomerServices customerServices;

    private LocalDate convertToLocalDate(Date dateToConvert) {
        return ((java.sql.Date) dateToConvert).toLocalDate();
    }

    /* ---------------------------------------------------------------- */
    // API For Maintenance
    // API to gel All Maintenance Details
    @GetMapping("/maintenance/Maintenancejson")
    public ResponseEntity<List<Maintenance>> getAllMaintenance() {
        List<Maintenance> maintenances = maintenanceServices.getAllMaintenance();
        return ResponseEntity.ok().body(maintenances);
    }

    @GetMapping("/maintenance/getAllCarstoMaintenance")
    public ResponseEntity<List<Vehicle>> getAllCarstoMaintenance(
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

    /* ---------------------------------------------------------------- */
    // API For Reservation
    // API to get all cars json data except the image
    @GetMapping("/reservation/getAllCars")
    public ResponseEntity<List<Vehicle>> getAllCars(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {

        List<Vehicle> vehicles = vehicleServices.getAllVehicles();

        // Filter vehicles based on search, status, and type
        List<Vehicle> filteredVehicles = vehicles.stream()
                .filter(vehicle -> (search == null
                        || vehicle.getVehicleModel().toLowerCase().contains(search.toLowerCase())) &&
                        (status == null || status.isEmpty() || vehicle.getVehicleStatus().equalsIgnoreCase(status)) &&
                        (type == null || type.isEmpty() || vehicle.getVehicleType().equalsIgnoreCase(type)))
                .collect(Collectors.toList());

        // Filter vehicles based on date availability
        if (startDate != null) {
            List<Reservation> reservations = reservationServices.getAllReservations();

            filteredVehicles = filteredVehicles.stream()
                    .filter(vehicle -> reservations.stream().noneMatch(reservation -> reservation.getVehicleID()
                            .equals(vehicle) &&
                            !(convertToLocalDate(reservation.getReservationEndDate()).isBefore(startDate))))
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

    @GetMapping("/reservation/getAvailableCarsToday")
    public ResponseEntity<List<Vehicle>> getAvailableCarsToday(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "sort", required = false) String sort) {

        List<Vehicle> vehicles = vehicleServices.getAllVehicles();

        // Get today's date
        LocalDate today = LocalDate.now();

        // Filter vehicles based on search, status, and type
        List<Vehicle> filteredVehicles = vehicles.stream()
                .filter(vehicle -> (search == null
                        || vehicle.getVehicleModel().toLowerCase().contains(search.toLowerCase())) &&
                        (status == null || status.isEmpty() || vehicle.getVehicleStatus().equalsIgnoreCase(status)) &&
                        (type == null || type.isEmpty() || vehicle.getVehicleType().equalsIgnoreCase(type)))
                .collect(Collectors.toList());

        // Filter vehicles based on availability today
        List<Reservation> reservations = reservationServices.getAllReservations();
        filteredVehicles = filteredVehicles.stream()
                .filter(vehicle -> reservations.stream().noneMatch(reservation -> reservation.getVehicleID()
                        .equals(vehicle) &&
                        !(convertToLocalDate(reservation.getReservationEndDate()).isBefore(today)
                                || convertToLocalDate(reservation.getReservationStartDate()).isAfter(today))))
                .collect(Collectors.toList());

        // Sort vehicles based on price
        if ("asc".equalsIgnoreCase(sort)) {
            filteredVehicles.sort(Comparator.comparingDouble(Vehicle::getVehicleReservedperHours));
        } else if ("desc".equalsIgnoreCase(sort)) {
            filteredVehicles.sort(Comparator.comparingDouble(Vehicle::getVehicleReservedperHours).reversed());
        }

        return ResponseEntity.ok().body(filteredVehicles);
    }

    // API to get all reservations json data except car image
    @GetMapping("/reservation/Reservationjson")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationServices.getAllReservations();
        return ResponseEntity.ok().body(reservations);
    }

    // API to get reservation json data by ID
    @GetMapping("/reservation/Reservationjson/{reservationID}")
    public ResponseEntity<Reservation> getReservation(@PathVariable int reservationID) {
        Reservation reservation = reservationServices.getReservation(reservationID);
        if (reservation != null) {
            return ResponseEntity.ok().body(reservation);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /* ---------------------------------------------------------------- */
    // API for Vehicle
    @GetMapping("/vehicle/vehiclejson/{vehicleID}")
    public ResponseEntity<Vehicle> getVehicle(@PathVariable int vehicleID) {
        Vehicle vehicle = vehicleServices.getVehicle(vehicleID);
        if (vehicle != null) {
            return ResponseEntity.ok().body(vehicle);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // API to get all cars json data except the image
    @GetMapping("/vehicle/allvehiclejson")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleServices.getAllVehicles();
        return ResponseEntity.ok().body(vehicles);
    }

    /* ---------------------------------------------------------------- */
    // API for Staff
    // API to get all staff json data
    @GetMapping("/staff/Staffjson")
    public ResponseEntity<List<Staff>> getAllStaff() {
        List<Staff> staffs = staffServices.getAllStaffs();
        return ResponseEntity.ok().body(staffs);
    }

    // API to get staff json data by ID
    @GetMapping("/staff/Staffjson/{staffID}")
    public ResponseEntity<Staff> getStaff(@PathVariable int staffID) {
        Staff staff = staffServices.getStaff(staffID);
        if (staff != null) {
            return ResponseEntity.ok().body(staff);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /* ---------------------------------------------------------------- */
    // API for Adminstrative
    // API to get all adminstrative json data
    @GetMapping("/adminstrative/Adminstrativejson")
    public ResponseEntity<List<Adminstrative>> getAllAdminstrative() {
        List<Adminstrative> adminstratives = adminstrativeServices.getAllAdminstrative();
        return ResponseEntity.ok().body(adminstratives);
    }

    // API to get adminstrative json data by ID
    @GetMapping("/adminstrative/Adminstrativejson/{adminID}")
    public ResponseEntity<Adminstrative> getAdminstrative(@PathVariable int adminID) {
        Adminstrative adminstrative = adminstrativeServices.getAdminstrative(adminID);
        if (adminstrative != null) {
            return ResponseEntity.ok().body(adminstrative);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /* ---------------------------------------------------------------- */

    // API to get all customer json data
    @GetMapping("/customer/Customerjson")
    public ResponseEntity<List<Customer>> getAllCustomer() {
        List<Customer> customers = customerServices.getAllCustomer();
        return ResponseEntity.ok().body(customers);
    }

    // API to get customer json data by ID
    @GetMapping("/customer/Customerjson/{custID}")
    public ResponseEntity<Customer> getCustomer(@PathVariable int custID) {
        Customer customer = customerServices.getCustomer(custID);
        if (customer != null) {
            return ResponseEntity.ok().body(customer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
