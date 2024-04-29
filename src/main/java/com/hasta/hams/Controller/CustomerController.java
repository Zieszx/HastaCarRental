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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@AllArgsConstructor(onConstructor = @__(@Autowired))
@NoArgsConstructor(force = true)
public class CustomerController {

    private CustomerServices customerServices;

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
    public String deleteCustomer(@RequestParam("Cust_ID") int id, HttpSession session) {
        if (session.getAttribute("user") == null)
            return "redirect:/";

        customerServices.deleteCustomer(id);

        return "redirect:/management/customers";
    }

    @GetMapping("/management/updateCustomer")
    public String updateCustomer(@RequestParam("Cust_ID") int id, Model model, HttpSession session) {
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
