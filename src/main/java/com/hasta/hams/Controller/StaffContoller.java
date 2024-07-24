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
import com.hasta.hams.model.Staff;
import com.hasta.hams.model.Vehicle;
import com.hasta.hams.service.CustomerServices;
import com.hasta.hams.service.StaffServices;

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
public class StaffContoller {

    private StaffServices staffServices;
    private ImageController imageController;

    /**
     * Retrieves all staffs and renders the "MainStaff" view.
     * 
     * @param model   the model object to add attributes to
     * @param session the HttpSession object to check user authentication
     * @return the name of the view to render
     */
    @GetMapping("/management/staffs")
    public String staffs(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        List<Staff> staffs = staffServices.getAllStaffs();
        model.addAttribute("staffs", staffs);
        return "Management/MainStaff";
    }

    /**
     * Renders the "AddStaff" view with an empty Staff object.
     * 
     * @param model   the model object to add attributes to
     * @param session the HttpSession object to check user authentication
     * @return the name of the view to render
     */
    @GetMapping("/management/addStaff")
    public String addStaff(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        model.addAttribute("staff", new Staff());
        return "Management/AddStaff";
    }

    /**
     * Adds a new staff to the database.
     * 
     * @param staff     the Staff object to add
     * @param imageFile the image file of the staff
     * @param model     the model object to add attributes to
     * @param session   the HttpSession object to check user authentication
     * @return the name of the view to render
     */
    @PostMapping("/management/addStaff")
    public String addStaff(@ModelAttribute Staff staff, @RequestParam("imageFile") MultipartFile imageFile,
            Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        staff.setStaffImage(imageController.setimageinDB(imageFile));
        staffServices.addStaff(staff);

        return "redirect:/management/staffs";
    }

    /**
     * Retrieves a staff by ID and renders the "UpdateStaff" view.
     * 
     * @param id      the ID of the staff to update
     * @param model   the model object to add attributes to
     * @param session the HttpSession object to check user authentication
     * @return the name of the view to render
     */
    @GetMapping("/management/updateStaff")
    public String updateStaff(@RequestParam("staffID") int id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        Staff staff = staffServices.getStaff(id);
        model.addAttribute("staff", staff);
        return "Management/UpdateStaff";
    }

    /**
     * Updates a staff in the database.
     * 
     * @param staff     the updated Staff object
     * @param password  the new password for the staff
     * @param imageFile the new image file for the staff
     * @param model     the model object to add attributes to
     * @param session   the HttpSession object to check user authentication
     * @return the name of the view to render
     */
    @PostMapping("/management/updateStaff")
    public String updateStaff(@ModelAttribute Staff staff, @RequestParam("staffPassword") String password,
            @RequestParam("imageFile") MultipartFile imageFile, Model model,
            HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        if (imageFile != null && !imageFile.isEmpty() && StringUtils.hasText(imageFile.getOriginalFilename()))
            staff.setStaffImage(imageController.setimageinDB(imageFile));
        else {
            Staff temp = staffServices.getStaff(staff.getStaffID());
            staff.setStaffImage(temp.getStaffImage());
        }

        Staff temp = staffServices.getStaff(staff.getStaffID());
        if (password.equals(""))
            staff.setStaffPassword(temp.getStaffPassword());
        else if (!temp.getStaffPassword().equals(password))
            staff.setStaffPassword(password);
        staffServices.updateStaff(staff);

        return "redirect:/management/staffs";
    }

    /**
     * Deletes a staff from the database.
     * 
     * @param id      the ID of the staff to delete
     * @param session the HttpSession object to check user authentication
     * @return the name of the view to render
     */
    @GetMapping("/management/deleteStaff")
    public String deleteStaff(@RequestParam("staffID") int id, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        staffServices.deleteStaff(id);

        return "redirect:/management/staffs";
    }

}
