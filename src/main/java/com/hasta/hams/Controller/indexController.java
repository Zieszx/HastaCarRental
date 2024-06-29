package com.hasta.hams.controller;

import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hasta.hams.model.Adminstrative;
import com.hasta.hams.model.Staff;
import com.hasta.hams.service.AdminstrativeServices;
import com.hasta.hams.service.StaffServices;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Controller
@AllArgsConstructor
public class IndexController {

    protected final AdminstrativeServices adminstrativeServices;
    protected final StaffServices staffServices;

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "index";
        return "redirect:/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, Model model) {
        model.addAttribute("error", null);
        session.invalidate();
        return "redirect:/";
    }

    @PostMapping("/login")
    public String login(Model model, @RequestParam("Username") String username,
            @RequestParam("Password") String password, HttpSession session) {

        List<Adminstrative> adminstratives = adminstrativeServices.getAllAdminstrative();
        List<Staff> staffs = staffServices.getAllStaffs();

        if (session.getAttribute("user") != null)
            return "redirect:/dashboard";

        boolean isUserlogged = false;

        for (Adminstrative adminstrative : adminstratives) {
            if (adminstrative.getAdminUsername().equals(username)
                    && adminstrative.getAdminPassword().equals(password)) {
                session.setAttribute("user", "adminstrative");
                session.setAttribute("adminID", adminstrative.getAdminID());
                isUserlogged = true;
                break;
            } else {
                isUserlogged = false;
            }
        }

        for (Staff staff : staffs) {
            if (staff.getStaffUsername().equals(username) && staff.getStaffPassword().equals(password)) {
                session.setAttribute("user", "staff");
                session.setAttribute("staffID", staff.getStaffID());
                isUserlogged = true;
                break;
            } else {
                isUserlogged = false;
            }
        }

        if (isUserlogged) {
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid Username or Password");
            return "index";
        }
    }

    @RequestMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        if (session.getAttribute("user").equals("adminstrative")) {
            Adminstrative adminstrative = adminstrativeServices.getAdminstrative((int) session.getAttribute("adminID"));
            model.addAttribute("adminstrative", adminstrative);
            return "/dashboard";
        } else if (session.getAttribute("user").equals("staff")) {
            Staff staff = staffServices.getStaff((int) session.getAttribute("staffID"));
            model.addAttribute("staff", staff);
            return "/dashboard";
        }
        return "/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";
        if (session.getAttribute("user").equals("adminstrative")) {
            Adminstrative adminstrative = adminstrativeServices.getAdminstrative((int) session.getAttribute("adminID"));
            model.addAttribute("adminstrative", adminstrative);
            return "profile/ProfileAdmin";
        } else if (session.getAttribute("user").equals("staff")) {
            Staff staff = staffServices.getStaff((int) session.getAttribute("staffID"));
            model.addAttribute("staff", staff);
            return "profile/ProfileStaff";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/forgotpassword")
    public String forgotPassword() {
        return "forgotpassword/forgotpassword";
    }

    @PostMapping("/forgotpassword")
    public String forgotpassword(@RequestParam("email") String email, Model model) {
        List<Adminstrative> adminstratives = adminstrativeServices.getAllAdminstrative();
        List<Staff> staffs = staffServices.getAllStaffs();

        for (Adminstrative adminstrative : adminstratives) {
            if (adminstrative.getAdminEmail().equals(email)) {
                model.addAttribute("password", adminstrative.getAdminPassword());
                model.addAttribute("user", "adminstrative");
                return "forgotpassword/validatedforgot";
            }
        }

        for (Staff staff : staffs) {
            if (staff.getStaffEmail().equals(email)) {
                model.addAttribute("password", staff.getStaffPassword());
                model.addAttribute("user", "staff");
                return "forgotpassword/validatedforgot";
            }
        }

        model.addAttribute("error", "Email not found");
        return "forgotpassword/forgotpassword";
    }

    @PostMapping("/changepassword")
    public String changePassword(@RequestParam("password") String password, @RequestParam("user") String user,
            Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        if (user.equals("adminstrative")) {
            Adminstrative adminstrative = adminstrativeServices.getAdminstrative((int) session.getAttribute("adminID"));
            adminstrative.setAdminPassword(password);
            adminstrativeServices.updateAdminstrative(adminstrative);
        } else if (user.equals("staff")) {
            Staff staff = staffServices.getStaff((int) session.getAttribute("staffID"));
            staff.setStaffPassword(password);
            staffServices.updateStaff(staff);
        }
        return "redirect:/";

    }

}
