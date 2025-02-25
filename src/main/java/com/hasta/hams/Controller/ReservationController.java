package com.hasta.hams.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import java.io.IOException;

import java.time.format.DateTimeFormatter;

/**
 * The ReservationController class handles the HTTP requests related to
 * reservations.
 * It provides methods for viewing cars, making reservations, managing
 * reservations, and updating reservations.
 */
@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
@NoArgsConstructor(force = true)
@RequestMapping("/reservation")
public class ReservationController {

    private final VehicleServices vehicleService;
    private final CustomerServices customerService;
    private final ReservationServices reservationService;
    private final PaymentServices paymentService;

    private ImageController imageController;
    private NotificationController notificationController;

    /**
     * Handles the request to view all cars.
     * 
     * @param model   the model to add attributes to.
     * @param session the HTTP session to check for user authentication.
     * @return the view name to display the cars.
     */
    @GetMapping("/viewCars")
    public String viewCars(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        model.addAttribute("vehicles", vehicles);
        return "Reservation/ViewCars";
    }

    /**
     * Handles the request to view cars available for a specific date range.
     * 
     * @param model   the model to add attributes to.
     * @param session the HTTP session to check for user authentication.
     * @return the view name to display the filtered cars.
     */
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

    /**
     * Handles the request to reserve a car for an exact date.
     * 
     * @param vehicleID the ID of the vehicle to reserve.
     * @param model     the model to add attributes to.
     * @param session   the HTTP session to check for user authentication.
     * @return the view name to display the reservation form.
     */
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

    /**
     * Handles the request to submit the reservation form for an exact date.
     * 
     * @param reservation    the reservation details.
     * @param vehicleID      the ID of the vehicle to reserve.
     * @param customerID     the ID of the customer making the reservation.
     * @param paymentDeposit the deposit amount for the reservation.
     * @param imageFile      the image file for the payment deposit.
     * @param result         the binding result to check for validation errors.
     * @param model          the model to add attributes to.
     * @param session        the HTTP session to check for user authentication.
     * @return the redirect URL after successful reservation.
     */
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

        vehicle.setVehicleStatus("Unavailable");
        vehicleService.updateVehicle(vehicle);

        // create notification message for specified vehicle
        String notificationMessage = "New reservation has made for vehicle: " + vehicle.getVehicleBrand() + " "
                + vehicle.getVehicleModel() + " " + vehicle.getVehicleLicensePlate();
        String notificationTitle = "New Reservation";
        String notificationType = "Reservation";

        // Create the notification
        notificationController.createNotification(notificationType, notificationMessage, notificationTitle);

        return "redirect:/reservation/viewCars";
    }

    /**
     * Handles the request to reserve a car for a specific date range.
     * 
     * @param vehicleID the ID of the vehicle to reserve.
     * @param model     the model to add attributes to.
     * @param session   the HTTP session to check for user authentication.
     * @return the view name to display the reservation form.
     */
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

    /**
     * Handles the request to submit the reservation form for a specific date range.
     * 
     * @param reservation    the reservation details.
     * @param vehicleID      the ID of the vehicle to reserve.
     * @param customerID     the ID of the customer making the reservation.
     * @param paymentDeposit the deposit amount for the reservation.
     * @param imageFile      the image file for the payment deposit.
     * @param result         the binding result to check for validation errors.
     * @param model          the model to add attributes to.
     * @param session        the HTTP session to check for user authentication.
     * @return the redirect URL after successful reservation.
     */
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

        vehicle.setVehicleStatus("Unavailable");
        vehicleService.updateVehicle(vehicle);

        // create notification message for specified vehicle
        String notificationMessage = "New reservation has made for vehicle: " + vehicle.getVehicleBrand() + " "
                + vehicle.getVehicleModel() + " " + vehicle.getVehicleLicensePlate();
        String notificationTitle = "New Reservation";
        String notificationType = "Reservation";

        // Create the notification
        notificationController.createNotification(notificationType, notificationMessage, notificationTitle);

        return "redirect:/reservation/viewCars";
    }

    /**
     * Handles the request to view all reservations.
     * 
     * @param model   the model to add attributes to.
     * @param session the HTTP session to check for user authentication.
     * @return the view name to display the reservations.
     */
    @GetMapping("/manageReservation")
    public String manageReservation(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        LocalDate today = LocalDate.now();

        List<Reservation> reservations = reservationService.getAllReservations();
        for (Reservation reservation : reservations) {
            if (reservation.getReservationEndDate().toLocalDate().isBefore(today)
                    && reservation.getReservationStatus().equals("Booked")) {
                reservation.setReservationStatus("Cancelled");
                reservation.setReservationReasonDeleted("Overdue or Expired reservation");
                reservationService.updateReservation(reservation);
            }
        }
        model.addAttribute("reservations", reservations);
        return "Reservation/ManageCarReservation";
    }

    /**
     * Handles the request to view a specific reservation.
     * 
     * @param reservationID the reservation ID to view.
     * @param model         on the model to add attributes to.
     * @param session       the HTTP session to check for user authentication.
     * @return the view name to display the reservation.
     */
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

    /**
     * Handles the request to update a reservation.
     * 
     * @param reservation the updated reservation details.
     * @param imageFile   the image file for the payment deposit.
     * @param result      the binding result to check for validation errors.
     * @param model       the model to add attributes to.
     * @param session     the HTTP session to check for user authentication.
     * @return the redirect URL after successful reservation update.
     */

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

        // create nofication message for specified vehicle`
        String notificationMessage = "Reservation has been updated for vehicle: "
                + reservation.getVehicleID().getVehicleBrand() + " " + reservation.getVehicleID().getVehicleModel()
                + " "
                + reservation.getVehicleID().getVehicleLicensePlate();
        String notificationTitle = "Reservation Updated";
        String notificationType = "Reservation";

        // Create the notification
        notificationController.createNotification(notificationType, notificationMessage, notificationTitle);

        return "redirect:/reservation/manageReservation";
    }

    /**
     * Handles the request to confirm a reservation.
     * 
     * @param reservationID      the reservation ID to confirm.
     * @param paymentAmount      the payment amount for the reservation.
     * @param fullPaymentImage   the image file for the full payment.
     * @param redirectAttributes the redirect attributes object to add flash
     *                           attributes to.
     * @return the redirect URL after successful reservation confirmation.
     */
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
        reservation.setReservationReasonDeleted("Null");
        try {
            reservationService.updateReservationStatus(reservation);
            redirectAttributes.addFlashAttribute("success", "Reservation updated successfully.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error updating reservation: " + e.getMessage());
        }

        // create nofication message for specified vehicle
        String notificationMessage = "Reservation has been confirmed for vehicle: "
                + reservation.getVehicleID().getVehicleBrand() + " " + reservation.getVehicleID().getVehicleModel()
                + " "
                + reservation.getVehicleID().getVehicleLicensePlate();
        String notificationTitle = "Reservation Confirmed";
        String notificationType = "Reservation";

        // Create the notification
        notificationController.createNotification(notificationType, notificationMessage, notificationTitle);

        return "redirect:/reservation/manageReservation";
    }

    /**
     * Handles the request to cancel a reservation.
     * 
     * @param reservationID            the reservation ID to cancel.
     * @param reservationReasonDeleted the reason for cancelling the reservation.
     * @param redirectAttributes       the redirect attributes object to add flash
     *                                 attributes to.
     * @return the redirect URL after successful reservation cancellation.
     */
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

        // create nofication message for specified vehicle
        String notificationMessage = "Reservation has been cancelled for vehicle: "
                + reservation.getVehicleID().getVehicleBrand() + " " + reservation.getVehicleID().getVehicleModel()
                + " "
                + reservation.getVehicleID().getVehicleLicensePlate();
        String notificationTitle = "Reservation Cancelled";
        String notificationType = "Reservation";

        // Create the notification
        notificationController.createNotification(notificationType, notificationMessage, notificationTitle);

        return "redirect:/reservation/manageReservation";
    }

    /**
     * Handles the request to return a car.
     * 
     * @param reservationID       the reservation ID to return.
     * @param paymentAmount       the payment amount for the reservation.
     * @param paymentDescriptions the description for the payment.
     * @param additionalImage     the image file for the additional payment.
     * @param redirectAttributes  the redirect attributes object to add flash
     *                            attributes to.
     * @return the redirect URL after successful car return.
     */
    @PostMapping("/returncar")
    public String returnCar(@RequestParam("reservationID") int reservationID,
            @RequestParam("paymentAmount") String paymentAmountStr,
            @RequestParam("paymentDescriptions") String paymentDescriptions,
            @RequestParam("additionalImage") MultipartFile additionalImage,
            RedirectAttributes redirectAttributes) {

        double paymentAmount;
        try {
            paymentAmount = Double.parseDouble(paymentAmountStr);
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "Invalid payment amount.");
            return "redirect:/reservation/manageReservation";
        }

        Reservation reservation = reservationService.getReservation(reservationID);
        Vehicle vehicle = vehicleService.getVehicle(reservation.getVehicleID().getVehicleID());
        Payment payment = paymentService.getPayment(reservation.getPaymentID().getPaymentId());

        payment.setPaymentAddtional(paymentAmount);
        payment.setPaymentDescriptions(paymentDescriptions);

        if (additionalImage != null && !additionalImage.isEmpty()
                && StringUtils.hasText(additionalImage.getOriginalFilename())) {
            payment.setPaymentAdditionalimage(imageController.setimageinDB(additionalImage));
        }

        try {
            reservationService.updateReservationStatus(reservation);
            redirectAttributes.addFlashAttribute("success", "Additional payment added successfully.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error adding additional payment: " + e.getMessage());
        }

        payment.setPaymentStatus("Successful Payment");
        paymentService.savePayment(payment);

        vehicle.setVehicleStatus("Available");
        vehicleService.updateVehicle(vehicle);

        reservation.setReservationStatus("Returned");
        reservation.setReservationReasonDeleted("Null");
        reservationService.updateReservation(reservation);

        redirectAttributes.addFlashAttribute("success", "Vehicle returned successfully.");

        // create notification message for specified vehicle
        String notificationMessage = "Vehicle has been returned for vehicle: "
                + reservation.getVehicleID().getVehicleBrand() + " " + reservation.getVehicleID().getVehicleModel()
                + " "
                + reservation.getVehicleID().getVehicleLicensePlate();
        String notificationTitle = "Vehicle Returned";
        String notificationType = "Reservation";

        // Create the notification
        notificationController.createNotification(notificationType, notificationMessage, notificationTitle);

        return "redirect:/reservation/manageReservation";
    }

    /**
     * Handles the request to delete a reservation.
     * 
     * @param reservationID      the reservation ID to delete.
     * @param redirectAttributes the redirect attributes object to add flash
     *                           attributes to.
     * @param model              the model to add attributes to.
     * @return the redirect URL after successful reservation deletion.
     */
    @GetMapping("/deletereservation")
    public String deleteReservation(@RequestParam("reservationID") int reservationID,
            RedirectAttributes redirectAttributes, Model model) {
        if (reservationService.getReservation(reservationID).getReservationStatus().equals("Booked")) {
            model.addAttribute("error", "Cannot delete a reservation that is not confirmed.");
            return "redirect:/reservation/manageReservation";
        }
        reservationService.deleteReservation(reservationID);
        redirectAttributes.addFlashAttribute("success", "Reservation deleted successfully.");
        return "redirect:/reservation/manageReservation";
    }

}
