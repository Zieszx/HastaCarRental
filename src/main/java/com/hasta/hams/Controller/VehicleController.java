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
    private ImageController imageController;

    @GetMapping("/management/vehicles")
    public String vehicles(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        List<Vehicle> vehicles = vehicleServices.getAllVehicles();
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

        vehicle.setVehicleImage(imageController.setimageinDB(imageFile));
        vehicleServices.addVehicle(vehicle);

        return "redirect:/management/addVehicle";
    }

    @GetMapping("/management/updateVehicle")
    public String editVehicle(@RequestParam("vehicleID") int id, Model model, HttpSession session) {
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
            vehicle.setVehicleImage(imageController.setimageinDB(imageFile));
        } else {
            Vehicle temp = vehicleServices.getVehicle(vehicle.getVehicleID());
            vehicle.setVehicleImage(temp.getVehicleImage());
        }
        vehicleServices.updateVehicle(vehicle);

        return "redirect:/management/vehicles";
    }

    @GetMapping("/management/deleteVehicle")
    public String deleteVehicle(@RequestParam("vehicleID") int id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        vehicleServices.deleteVehicle(id);

        return "redirect:/management/vehicles";
    }

}
