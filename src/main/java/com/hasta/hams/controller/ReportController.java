package com.hasta.hams.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hasta.hams.model.Adminstrative;
import com.hasta.hams.model.Customer;
import com.hasta.hams.model.Maintenance;
import com.hasta.hams.model.Reservation;
import com.hasta.hams.model.Staff;
import com.hasta.hams.model.Vehicle;
import com.hasta.hams.service.AdminstrativeServices;
import com.hasta.hams.service.CustomerServices;
import com.hasta.hams.service.MaintenanceServices;
import com.hasta.hams.service.ReservationServices;
import com.hasta.hams.service.StaffServices;
import com.hasta.hams.service.VehicleServices;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private CustomerServices customerServices;
    private StaffServices staffServices;
    private AdminstrativeServices adminstrativeServices;
    private VehicleServices vehicleServices;
    private ReservationServices reservationServices;
    private MaintenanceServices maintenanceServices;

    @GetMapping("/generatereport")
    public String generateReport(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        // Customer Report
        List<Customer> customers = customerServices.getAllCustomer();
        List<Reservation> reservations = reservationServices.getAllReservations();
        List<Integer> mostCarReservations = new ArrayList<>();
        int reservedbyCustomer = 0;
        for (Customer customer : customers) {
            reservedbyCustomer = (int) reservations.stream()
                    .filter(reservation -> reservation.getCustomerID().equals(customer)
                            && reservation.getReservationStatus().equals("Returned"))
                    .count();
            mostCarReservations.add(reservedbyCustomer);
        }

        // Maintenance Report
        List<Maintenance> maintenances = maintenanceServices.getAllMaintenance();

        // Vehicle Report
        List<Vehicle> vehicles = vehicleServices.getAllVehicles();
        List<List<Integer>> vehicleReservationAndMaintenanceIntegers = new ArrayList<>();

        for (Vehicle vehicle : vehicles) {
            int vehicleReservation = (int) reservations.stream()
                    .filter(reservation -> reservation.getVehicleID().equals(vehicle)
                            && reservation.getReservationStatus().equals("Returned"))
                    .count();

            int vehicleMaintenance = (int) maintenances.stream()
                    .filter(maintenance -> maintenance.getVehicleID().equals(vehicle)
                            && maintenance.getMaintenanceStatus().equals("Complete"))
                    .count();

            List<Integer> vehicleData = new ArrayList<>();
            vehicleData.add(vehicleReservation);
            vehicleData.add(vehicleMaintenance);

            vehicleReservationAndMaintenanceIntegers.add(vehicleData);
        }

        // Staff Report
        List<Staff> staffs = staffServices.getAllStaffs();

        // Customer
        model.addAttribute("customers", customers);
        model.addAttribute("mostCarReservations", mostCarReservations);

        // Maintenance
        model.addAttribute("maintenances", maintenances);

        // Vehicle
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("vehicleReservationAndMaintenanceIntegers", vehicleReservationAndMaintenanceIntegers);

        // Staff
        model.addAttribute("staffs", staffs);

        return "Management/Generatereport";
    }

    @GetMapping("/customer")
    public ResponseEntity<InputStreamResource> customerReport() throws FileNotFoundException {
        String csvFile = new String("customer.csv");
        FileInputStream fis = new FileInputStream(csvFile);
        InputStreamResource resource = new InputStreamResource(fis);

        List<Customer> customers = customerServices.getAllCustomer();
        List<Reservation> reservations = reservationServices.getAllReservations();
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append("Customer ID,Customer Name,Customer Phone,Customer Email,Most Car Reservations\n");

            for (Customer customer : customers) {
                long mostCarReservations = reservations.stream()
                        .filter(reservation -> reservation.getCustomerID().equals(customer))
                        .count();

                writer.append(String.join(",",
                        String.valueOf(customer.getCustID()),
                        customer.getCustName(),
                        customer.getCustPhone(),
                        customer.getCustEmail(),
                        String.valueOf(mostCarReservations)))
                        .append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + csvFile)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(resource);
    }
}