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

    /**
     * Handles the request for the home page.
     * If the user is not logged in, it returns the "index" view.
     * If the user is logged in, it redirects to the dashboard.
     *
     * @param model   the model object for the view
     * @param session the HttpSession object for session management
     * @return the view name to be rendered
     */
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "index";
        return "redirect:/dashboard";
    }

    /**
     * Handles the request for logging out.
     * Invalidates the session and redirects to the home page.
     *
     * @param session the HttpSession object for session management
     * @param model   the model object for the view
     * @return the view name to be rendered
     */
    @GetMapping("/logout")
    public String logout(HttpSession session, Model model) {
        model.addAttribute("error", null);
        session.invalidate();
        return "redirect:/";
    }

    /**
     * Handles the request for logging in.
     * Checks the username and password provided and sets the appropriate session
     * attributes.
     * If the login is successful, it redirects to the dashboard.
     * If the login fails, it returns the "index" view with an error message.
     *
     * @param model    the model object for the view
     * @param username the username parameter from the login form
     * @param password the password parameter from the login form
     * @param session  the HttpSession object for session management
     * @return the view name to be rendered
     */
    @PostMapping("/")
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

        if (!isUserlogged) {
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
        }

        if (isUserlogged) {
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid Username or Password");
            return "index";
        }
    }

    /**
     * Handles the request for the dashboard.
     * If the user is not logged in, it redirects to the home page.
     * If the user is logged in as an administrative user, it retrieves the
     * administrative user's details and adds them to the model.
     * If the user is logged in as a staff user, it retrieves the staff user's
     * details and adds them to the model.
     *
     * @param model   the model object for the view
     * @param session the HttpSession object for session management
     * @return the view name to be rendered
     */
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

    /**
     * Handles the request for the profile page.
     * If the user is not logged in, it redirects to the home page.
     * If the user is logged in as an administrative user, it retrieves the
     * administrative user's details and adds them to the model.
     * If the user is logged in as a staff user, it retrieves the staff user's
     * details and adds them to the model.
     *
     * @param model   the model object for the view
     * @param session the HttpSession object for session management
     * @return the view name to be rendered
     */
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

    /**
     * Handles the request for the forgot password page.
     *
     * @return the view name to be rendered
     */
    @GetMapping("/forgotpassword")
    public String forgotPassword() {
        return "forgotpassword/forgotpassword";
    }

    /**
     * Handles the request for validating the forgot password form.
     * Checks if the provided email exists in the administrative or staff users.
     * If the email is found, it adds the password and user type to the model and
     * returns the validatedforgot view.
     * If the email is not found, it adds an error message to the model and returns
     * the forgotpassword view.
     *
     * @param email the email parameter from the forgot password form
     * @param model the model object for the view
     * @return the view name to be rendered
     */
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

    /**
     * Handles the request for changing the password.
     * If the user is not logged in, it redirects to the home page.
     * If the user is logged in as an administrative user, it updates the
     * administrative user's password.
     * If the user is logged in as a staff user, it updates the staff user's
     * password.
     *
     * @param password the new password parameter from the change password form
     * @param user     the user type parameter from the change password form
     * @param model    the model object for the view
     * @param session  the HttpSession object for session management
     * @return the view name to be rendered
     */
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
