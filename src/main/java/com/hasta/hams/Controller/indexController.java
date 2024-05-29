package com.hasta.hams.Controller;

import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hasta.hams.Model.Adminstrative;
import com.hasta.hams.Model.Staff;
import com.hasta.hams.Service.AdminstrativeServices;
import com.hasta.hams.Service.StaffServices;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Controller
@AllArgsConstructor
public class indexController {

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
        List<Staff> staffs = staffServices.getAllStaff();

        if (session.getAttribute("user") != null)
            return "redirect:/dashboard";

        boolean isUserlogged = false;

        for (Adminstrative adminstrative : adminstratives) {
            if (adminstrative.getAdminUsername().equals(username)
                    && adminstrative.getAdminPassword().equals(password)) {
                session.setAttribute("user", adminstrative);
                isUserlogged = true;
                break;
            } else {
                isUserlogged = false;
            }
        }

        for (Staff staff : staffs) {
            if (staff.getStaffUsername().equals(username) && staff.getStaffPassword().equals(password)) {
                session.setAttribute("user", staff);
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
            return "redirect:/";
        }
    }

    @RequestMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";
        return "/dashboard";
    }

}
