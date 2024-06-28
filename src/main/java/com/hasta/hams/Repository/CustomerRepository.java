package com.hasta.hams.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hasta.hams.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    boolean existsByCustEmail(String Cust_Email);

}
