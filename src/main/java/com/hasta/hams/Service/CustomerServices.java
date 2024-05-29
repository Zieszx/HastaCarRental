package com.hasta.hams.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hasta.hams.Model.Customer;
import com.hasta.hams.Repository.CustomerRepository;

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

    public Customer getCustomer(int id) {
        return customerRepository.findById(id).get();
    }

    public void addCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    public void updateCustomer(Customer customer) {
        customerRepository.save(customer);
    }

    public void deleteCustomer(int id) {
        customerRepository.deleteById(id);
    }

    public boolean emailExists(String custEmail) {
        return customerRepository.existsByCustEmail(custEmail);
    }

}
