package com.hasta.hams.controller;

import java.io.IOException;

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

import com.hasta.hams.model.Payment;
import com.hasta.hams.model.Staff;
import com.hasta.hams.model.Vehicle;
import com.hasta.hams.service.PaymentServices;
import com.hasta.hams.service.ReservationServices;
import com.hasta.hams.service.StaffServices;
import com.hasta.hams.service.VehicleServices;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
@NoArgsConstructor(force = true)
public class ImageController {
    private VehicleServices vehicleServices;
    private PaymentServices paymentServices;
    private ReservationServices reservationServices;
    private StaffServices staffServices;

    public byte[] setimageinDB(MultipartFile tempfile) {
        byte[] imageBytes = null;
        if (tempfile != null && !tempfile.isEmpty()) {
            try {
                imageBytes = tempfile.getBytes();
            } catch (IOException e) {
                // Handle the exception
                e.printStackTrace(); // Log or handle the exception appropriately
            }
        }
        return imageBytes;
    }

    @GetMapping("/payment/paymentImage")
    public ResponseEntity<ByteArrayResource> displayVehicleImage(@RequestParam("paymentID") int id,
            @RequestParam("paymentType") String paymentType) {
        Payment payment = paymentServices.getPayment(id);
        byte[] image = null;
        if (paymentType.equals("deposit")) {
            image = payment.getPaymentDepositimage();
        } else if (paymentType.equals("full")) {
            image = payment.getPaymentFullimage();
        } else if (paymentType.equals("additional")) {
            image = payment.getPaymentAdditionalimage();
        }
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new ByteArrayResource(image));
    }

    @GetMapping("/vehicle/displayVehicleImage")
    public ResponseEntity<ByteArrayResource> displayVehicleImage(@RequestParam("vehicleID") int id)
            throws IOException {
        Vehicle vehicle = vehicleServices.getVehicle(id);
        byte[] image = vehicle.getVehicleImage();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new ByteArrayResource(image));
    }

    @GetMapping("/staff/displayStaffImage")
    public ResponseEntity<ByteArrayResource> displayStaffImage(@RequestParam("staffID") int id) {
        Staff staff = staffServices.getStaff(id);
        byte[] image = staff.getStaffImage();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new ByteArrayResource(image));
    }

}
