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

import com.hasta.hams.Model.Customer;
import com.hasta.hams.Model.Staff;
import com.hasta.hams.Service.StaffServices;
import com.hasta.hams.Service.CustomerServices;

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

    @GetMapping("/management/staffs")
    public String staffs(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        List<Staff> staffs = staffServices.getAllStaffs();
        model.addAttribute("staffs", staffs);
        return "Management/MainStaff";
    }

    @GetMapping("/management/addStaff")
    public String addStaff(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        model.addAttribute("staff", new Staff());
        return "Management/AddStaff";
    }

    @PostMapping("/management/addStaff")
    public String addStaff(@ModelAttribute Staff staff, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        staffServices.addStaff(staff);

        return "redirect:/management/staffs";
    }

    @GetMapping("/management/updateStaff")
    public String updateStaff(@RequestParam("staffID") int id, Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        Staff staff = staffServices.getStaff(id);
        model.addAttribute("staff", staff);
        return "Management/UpdateStaff";
    }

    @PostMapping("/management/updateStaff")
    public String updateStaff(@ModelAttribute Staff staff, @RequestParam("staffPassword") String password, Model model,
            HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        Staff temp = staffServices.getStaff(staff.getStaffID());
        if (password.equals(""))
            staff.setStaffPassword(temp.getStaffPassword());
        else if (!temp.getStaffPassword().equals(password))
            staff.setStaffPassword(password);
        staffServices.updateStaff(staff);

        return "redirect:/management/staffs";
    }

    @GetMapping("/management/deleteStaff")
    public String deleteStaff(@RequestParam("staffID") int id, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        staffServices.deleteStaff(id);

        return "redirect:/management/staffs";
    }

}
