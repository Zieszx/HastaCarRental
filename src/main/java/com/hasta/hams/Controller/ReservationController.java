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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

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

}
