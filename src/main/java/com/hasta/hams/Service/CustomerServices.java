package com.hasta.hams.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hasta.hams.model.Customer;
import com.hasta.hams.repository.CustomerRepository;

import java.util.List;
import java.util.ArrayList;

@Service
public class CustomerServices {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomer() {
        List<Customer> customers = new ArrayList<>();
        customerRepository.findAll().forEach(customers::add);
        return customers;
    }

    public Customer getCustomer(int customerId) {
        return customerRepository.findById(customerId).orElse(null);
    }

    public void addCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    public void updateCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    public void deleteCustomer(int customerId) {
        customerRepository.deleteById(customerId);
    }

    public boolean emailExists(String custEmail) {
        return customerRepository.existsByCustEmail(custEmail);
    }

}
