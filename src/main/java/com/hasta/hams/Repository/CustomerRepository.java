package com.hasta.hams.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hasta.hams.Model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    boolean existsByCustEmail(String Cust_Email);

}
