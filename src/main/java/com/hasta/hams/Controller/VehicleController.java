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

import com.hasta.hams.Model.Vehicle;
import com.hasta.hams.Service.VehicleServices;

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
public class VehicleController {

    private VehicleServices vehicleServices;

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

    @GetMapping("/vehicle/displayVehicleImage")
    public ResponseEntity<ByteArrayResource> displayVehicleImage(@RequestParam("Vehicle_ID") int id)
            throws IOException {
        Vehicle vehicle = vehicleServices.getVehicle(id);
        byte[] image = vehicle.getVehicle_Image();
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(new ByteArrayResource(image));
    }

    @GetMapping("/management/vehicles")
    public String vehicles(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        List<Vehicle> vehicles = vehicleServices.getAllVehicle();
        model.addAttribute("vehicles", vehicles);
        return "Management/MainVehicle";
    }

    @GetMapping("/management/addVehicle")
    public String addVehicle(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        model.addAttribute("vehicle", new Vehicle());
        return "Management/AddVehicle";
    }

    @PostMapping("/management/addVehicle")
    public String addVehicle(@ModelAttribute Vehicle vehicle, @RequestParam("imageFile") MultipartFile imageFile,
            Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        vehicle.setVehicle_Image(setimageinDB(imageFile));
        vehicleServices.addVehicle(vehicle);

        return "redirect:/management/addVehicle";
    }

    @GetMapping("/management/updateVehicle")
    public String editVehicle(@RequestParam("Vehicle_ID") int id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        Vehicle vehicle = vehicleServices.getVehicle(id);
        model.addAttribute("vehicle", vehicle);
        return "Management/UpdateVehicle";
    }

    @PostMapping("/management/editVehicle")
    public String editVehicle(@ModelAttribute Vehicle vehicle, @RequestParam("imageFile") MultipartFile imageFile,
            Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        if (imageFile != null && !imageFile.isEmpty() && StringUtils.hasText(imageFile.getOriginalFilename())) {
            vehicle.setVehicle_Image(setimageinDB(imageFile));
        } else {
            Vehicle temp = vehicleServices.getVehicle(vehicle.getVehicle_ID());
            vehicle.setVehicle_Image(temp.getVehicle_Image());
        }
        vehicleServices.updateVehicle(vehicle);

        return "redirect:/management/vehicles";
    }

    @GetMapping("/management/deleteVehicle")
    public String deleteVehicle(@RequestParam("Vehicle_ID") int id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        vehicleServices.deleteVehicle(id);

        return "redirect:/management/vehicles";
    }

}
