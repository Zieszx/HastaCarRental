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

    // api for getting all customers from the database
    @GetMapping("/api/checkEmail")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean exists = customerServices.emailExists(email);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }

    @GetMapping("/home/registercustomer")
    public String registerCustomer(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        model.addAttribute("customer", new Customer());
        return "Register/RegisterCustomer";
    }

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

    @GetMapping("/management/customers")
    public String customers(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        List<Customer> customers = customerServices.getAllCustomer();
        model.addAttribute("customers", customers);

        return "Management/MainCustomer";
    }

    @GetMapping("/management/deleteCustomer")
    public String deleteCustomer(@RequestParam("custID") int id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        Customer customer = customerServices.getCustomer(id);

        List<Reservation> reservations = reservationServices.getReservationByCustomer(customer);

        for (Reservation reservation : reservations) {
            // Booked, Confirmed, Returned, Refund, Cancelled
            if (reservation.getReservationStatus().equals("Booked")
                    || reservation.getReservationStatus().equals("Confirmed")) {
                model.addAttribute("message", "Cannot delete customer with active reservations");
                return "redirect:/management/customers";
            }
        }

        customerServices.deleteCustomer(id);

        return "redirect:/management/customers";
    }

    @GetMapping("/management/updateCustomer")
    public String updateCustomer(@RequestParam("custID") int id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        Customer customer = customerServices.getCustomer(id);
        model.addAttribute("customer", customer);

        return "Management/UpdateCustomer";
    }

    @PostMapping("/management/updateCustomer")
    public String updateCustomer(@ModelAttribute Customer customer, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        customerServices.updateCustomer(customer);

        return "redirect:/management/customers";
    }

}
