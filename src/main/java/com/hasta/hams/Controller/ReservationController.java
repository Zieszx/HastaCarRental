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
@RequestMapping("/reservation")
public class ReservationController {

    private final VehicleServices vehicleService;
    private final CustomerServices customerService;
    private final ReservationServices reservationService;
    private final PaymentServices paymentService;
    private final StaffServices staffServices;

    private ImageController imageController;

    @GetMapping("/viewCars")
    public String viewCars(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        model.addAttribute("vehicles", vehicles);
        return "Reservation/ViewCars";
    }

    @GetMapping("/viewCars/exactDate")
    public String viewCarsExactDate(Model model, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/";
        }

        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        List<Vehicle> filteredVehicles = new ArrayList<>(); // filtered vehicles based on date availability
        List<Reservation> reservations = reservationService.getAllReservations();

        LocalDate startDate = LocalDate.now(); // Replace with actual start date if needed
        LocalDate endDate = LocalDate.now();// Replace with actual end date if needed

        for (Vehicle vehicle : vehicles) {
            boolean isAvailable = true;

            for (Reservation reservation : reservations) {
                if (reservation.getVehicleID().getVehicleID() == vehicle.getVehicleID()) {
                    LocalDate reservationStartDate = reservation.getReservationStartDate().toLocalDate();
                    LocalDate reservationEndDate = reservation.getReservationEndDate().toLocalDate();

                    // Check if the reservation overlaps with the desired date range
                    if ((startDate.isBefore(reservationEndDate) || startDate.isEqual(reservationEndDate)) &&
                            (endDate.isAfter(reservationStartDate) || endDate.isEqual(reservationStartDate))) {
                        isAvailable = false;
                        break;
                    }
                }
            }

            if (isAvailable) {
                filteredVehicles.add(vehicle);
            }
        }

        model.addAttribute("vehicles", filteredVehicles);
        return "Reservation/ViewCarExactDate";
    }

    @GetMapping("/reservedCar/exactDate")
    public String reservedCarExactDate(@RequestParam("vehicleID") int vehicleID, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        List<Customer> customers = customerService.getAllCustomer();
        model.addAttribute("customers", customers);

        Vehicle vehicle = vehicleService.getVehicle(vehicleID);
        model.addAttribute("vehicle", vehicle);

        java.sql.Date startDate = java.sql.Date.valueOf(LocalDate.now());

        Reservation reservation = new Reservation();
        reservation.setReservationStartDate(startDate);
        reservation.setReservationEndDate(startDate);

        model.addAttribute("reservation", reservation);
        return "Reservation/ReservedCarExactDate";
    }

    @PostMapping("/reservedCar/exactDate")
    public String reservedCarExactDate(@ModelAttribute Reservation reservation,
            @RequestParam("vehicleID") int vehicleID,
            @RequestParam("customerID") int customerID, @RequestParam("paymentDeposit") double paymentDeposit,
            @RequestParam("imageFile") MultipartFile imageFile, BindingResult result, Model model,
            HttpSession session) {
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

    @GetMapping("/reservedCar/specificDate")
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

    @PostMapping("/reservedCar/specificDate")
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
        payment.setPaymentDepositimage(imageController.setimageinDB(imageFile));
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

    @GetMapping("/manageReservation")
    public String manageReservation(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        List<Reservation> reservations = reservationService.getAllReservations();
        model.addAttribute("reservations", reservations);
        return "Reservation/ManageCarReservation";
    }

    @GetMapping("/updatereservation")
    public String updateReservation(@RequestParam("reservationID") int reservationID, Model model,
            HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        Reservation reservation = reservationService.getReservation(reservationID);
        Vehicle vehicle = vehicleService.getVehicle(reservation.getVehicleID().getVehicleID());
        Customer customer = customerService.getCustomer(reservation.getCustomerID().getCustID());
        Payment payment = paymentService.getPayment(reservation.getPaymentID().getPaymentId());

        if (payment.getPaymentDepositimage() != null) {
            model.addAttribute("depositImage", true);
        } else if (payment.getPaymentFullimage() != null) {
            model.addAttribute("fullImage", true);
        } else if (payment.getPaymentAdditionalimage() != null) {
            model.addAttribute("additionalImage", true);
        }

        model.addAttribute("reservation", reservation);
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("customer", customer);
        model.addAttribute("payment", payment);

        return "Reservation/UpdateCarReservation";
    }

    @PostMapping("/updatereservation")
    public String updateReservation(@ModelAttribute Reservation reservation,
            @RequestParam("imageFile") MultipartFile imageFile,
            BindingResult result, Model model,
            HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        double depo = reservation.getPaymentID().getPaymentDeposit();
        Payment paymentID = reservation.getPaymentID();

        Payment payment = paymentService.getPayment(paymentID.getPaymentId());

        if (imageFile != null && !imageFile.isEmpty() && StringUtils.hasText(imageFile.getOriginalFilename())) {
            payment.setPaymentDepositimage(imageController.setimageinDB(imageFile));
        }
        payment.setPaymentDeposit(depo);
        paymentService.savePayment(payment);

        reservationService.updateReservation(reservation);

        return "redirect:/reservation/manageReservation";
    }

    @PostMapping("/confirmreservation")
    public String updateReservation(@RequestParam("reservationID") int reservationID,
            @RequestParam("paymentAmount") double paymentAmount,
            @RequestParam("fullPaymentImage") MultipartFile fullPaymentImage,
            RedirectAttributes redirectAttributes) {

        Reservation reservation = reservationService.getReservation(reservationID);
        Payment payment = paymentService.getPayment(reservation.getPaymentID().getPaymentId());
        payment.setPaymentAmount(paymentAmount);
        payment.setPaymentStatus("Confirmed Payment");
        payment.setPaymentFullimage(imageController.setimageinDB(fullPaymentImage));
        paymentService.savePayment(payment);
        reservation.setReservationStatus("Confirmed");
        try {
            reservationService.updateReservationStatus(reservation);
            redirectAttributes.addFlashAttribute("success", "Reservation updated successfully.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error updating reservation: " + e.getMessage());
        }
        return "redirect:/reservation/manageReservation";
    }

    @PostMapping("/additionalpayment")
    public String addAdditionalPayment(@RequestParam("reservationID") int reservationID,
            @RequestParam("paymentAmount") double paymentAmount,
            @RequestParam("paymentDescriptions") String paymentDescriptions,
            @RequestParam("additionalImage") MultipartFile additionalImage,
            RedirectAttributes redirectAttributes) {

        Reservation reservation = reservationService.getReservation(reservationID);
        Payment payment = paymentService.getPayment(reservation.getPaymentID().getPaymentId());
        payment.setPaymentAddtional(paymentAmount);
        payment.setPaymentDescriptions(paymentDescriptions);
        payment.setPaymentAdditionalimage(imageController.setimageinDB(additionalImage));
        try {
            reservationService.updateReservationStatus(reservation);
            redirectAttributes.addFlashAttribute("success", "Additional payment added successfully.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error adding additional payment: " + e.getMessage());
        }
        return "redirect:/reservation/manageReservation";
    }

    @PostMapping("/cancelreservation")
    public String cancelReservation(@RequestParam("reservationID") int reservationID,
            @RequestParam("reservationReasonDeleted") String reservationReasonDeleted,
            RedirectAttributes redirectAttributes) {
        Reservation reservation = reservationService.getReservation(reservationID);
        reservation.setReservationStatus("Cancelled");
        reservation.setReservationReasonDeleted(reservationReasonDeleted);
        try {
            reservationService.updateReservationStatus(reservation);
            redirectAttributes.addFlashAttribute("success", "Reservation cancelled successfully.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error cancelling reservation: " + e.getMessage());
        }
        return "redirect:/reservation/manageReservation";
    }

    @PostMapping("/returncar")
    public String returnCar(@RequestParam("reservationID") int reservationID,
            RedirectAttributes redirectAttributes) {

        Reservation reservation = reservationService.getReservation(reservationID);
        Vehicle vehicle = vehicleService.getVehicle(reservation.getVehicleID().getVehicleID());
        Payment payment = paymentService.getPayment(reservation.getPaymentID().getPaymentId());
        payment.setPaymentStatus("Returned");
        paymentService.savePayment(payment);

        vehicle.setVehicleStatus("Available");
        vehicleService.updateVehicle(vehicle);

        reservation.setReservationStatus("Returned");
        reservationService.updateReservation(reservation);

        return "redirect:/reservation/manageReservation";
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

        List<Vehicle> vehicles = vehicleService.getAllVehicles();

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

    @GetMapping("/getAvailableCarsToday")
    public ResponseEntity<List<Vehicle>> getAvailableCarsToday(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "sort", required = false) String sort) {

        List<Vehicle> vehicles = vehicleService.getAllVehicles();

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
        List<Reservation> reservations = reservationService.getAllReservations();
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

}
