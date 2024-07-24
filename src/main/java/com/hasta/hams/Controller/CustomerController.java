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

import com.hasta.hams.model.Customer;
import com.hasta.hams.model.Notification;
import com.hasta.hams.model.Reservation;
import com.hasta.hams.service.CustomerServices;
import com.hasta.hams.service.NotificationServices;
import com.hasta.hams.service.ReservationServices;

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
public class CustomerController {

    private CustomerServices customerServices;
    private ReservationServices reservationServices;
    private NotificationController notificationController;

    /**
     * API for checking if an email exists in the database.
     *
     * @param email The email to check.
     * @return ResponseEntity<Boolean> indicating whether the email exists or not.
     */
    @GetMapping("/api/checkEmail")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = customerServices.emailExists(email);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    /**
     * Handles the GET request for registering a new customer.
     *
     * @param model   The model to add attributes to.
     * @param session The HttpSession object.
     * @return The view name for registering a new customer.
     */
    @GetMapping("/home/registercustomer")
    public String registerCustomer(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        model.addAttribute("customer", new Customer());
        return "Register/RegisterCustomer";
    }

    /**
     * Handles the POST request for registering a new customer.
     *
     * @param customer The Customer object to register.
     * @param model    The model to add attributes to.
     * @param session  The HttpSession object.
     * @return The view name for redirecting after registering a new customer.
     */
    @PostMapping("/home/registercustomer")
    public String registerCustomer(@ModelAttribute Customer customer, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        // Create notification message
        String notificationMessage = "New customer added: " + customer.getCustName();
        String notificationTitle = "Customer Registration";
        String notificationType = "Customer";

        // Create the notification
        notificationController.createNotification(notificationType, notificationMessage, notificationTitle);
        customerServices.addCustomer(customer);

        return "redirect:/home/registercustomer";
    }

    /**
     * Handles the GET request for displaying all customers.
     *
     * @param model   The model to add attributes to.
     * @param session The HttpSession object.
     * @return The view name for displaying all customers.
     */
    @GetMapping("/management/customers")
    public String customers(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        List<Customer> customers = customerServices.getAllCustomer();
        model.addAttribute("customers", customers);

        return "Management/MainCustomer";
    }

    /**
     * Handles the GET request for deleting a customer.
     *
     * @param id      The ID of the customer to delete.
     * @param session The HttpSession object.
     * @return ResponseEntity<Map<String, String>> indicating the status and message
     *         of the deletion.
     */
    @GetMapping("/management/deleteCustomer")
    public ResponseEntity<Map<String, String>> deleteCustomer(@RequestParam("custID") int id, HttpSession session) {
        Map<String, String> response = new HashMap<>();
        if (session.getAttribute("user") == null) {
            response.put("status", "error");
            response.put("message", "User not logged in.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Customer customer = customerServices.getCustomer(id);
        List<Reservation> reservations = reservationServices.getReservationByCustomer(customer);

        for (Reservation reservation : reservations) {
            if (reservation.getReservationStatus().equals("Booked") ||
                    reservation.getReservationStatus().equals("Confirmed")) {
                response.put("status", "error");
                response.put("message", "Cannot delete customer with active reservations.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        }

        for (Reservation reservation : reservations) {
            reservationServices.deleteReservation(reservation.getReservationID());
        }

        customerServices.deleteCustomer(id);
        response.put("status", "success");
        response.put("message", "Customer deleted successfully.");
        return ResponseEntity.ok(response);
    }

    /**
     * Handles the GET request for updating a customer.
     *
     * @param id      The ID of the customer to update.
     * @param model   The model to add attributes to.
     * @param session The HttpSession object.
     * @return The view name for updating a customer.
     */
    @GetMapping("/management/updateCustomer")
    public String updateCustomer(@RequestParam("custID") int id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        Customer customer = customerServices.getCustomer(id);
        model.addAttribute("customer", customer);

        return "Management/UpdateCustomer";
    }

    /**
     * Handles the POST request for updating a customer.
     *
     * @param customer The Customer object to update.
     * @param model    The model to add attributes to.
     * @param session  The HttpSession object.
     * @return The view name for redirecting after updating a customer.
     */
    @PostMapping("/management/updateCustomer")
    public String updateCustomer(@ModelAttribute Customer customer, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        customerServices.updateCustomer(customer);

        return "redirect:/management/customers";
    }

}
