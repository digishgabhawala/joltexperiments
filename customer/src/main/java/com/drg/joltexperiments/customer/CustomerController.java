package com.drg.joltexperiments.customer;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping
    public List<Customer> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        logger.debug("Found {} customers", customers.size());
        customers.forEach(c -> logger.debug("Customer: {}", c));
        return customers;
    }

    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Long id) {
        logger.debug("Get customer with id {}", id);
        Optional<Customer> customer = customerRepository.findById(id);
        if (!customer.isPresent()) {
            logger.error("Customer not found with id {}", id);
            throw new IllegalArgumentException("Customer not found with id " + id);
        }
        Customer customer1 = customer.get();
        logger.debug("Found customer: {}", customer1);
        return customer1;
    }

    @PostMapping
    public Customer createCustomer(@RequestBody Customer customer) {
        logger.debug("Create customer: {}", customer);
        return customerRepository.save(customer);
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer updatedCustomer) {
        logger.debug("Update customer with id {}: {}", id, updatedCustomer);
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Customer not found with id " + id));
        existingCustomer.setFirstName(updatedCustomer.getFirstName());
        existingCustomer.setLastName(updatedCustomer.getLastName());
        existingCustomer.setEmail(updatedCustomer.getEmail());
        return customerRepository.save(existingCustomer);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id)
    {
        logger.debug("Delete customer with id {}", id);
        customerRepository.deleteById(id);
    }
    @GetMapping("/reset")
    public List<Customer> reset() {
        customerRepository.truncate();
        return getAllCustomers();
    }
}
