package com.drg.joltexperiments.customer;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;

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


    @GetMapping("/search")
    public ResponseEntity<List<Customer>> searchCustomersByName(
            @RequestHeader(value = "key") String key,
            @RequestParam String name,
            @RequestHeader(value = "X-Page", defaultValue = "1") int page,
            @RequestHeader(value = "X-Size", defaultValue = "10") int size,
            @RequestHeader(value = "email", required = false) String email) {

        // Validate the key header
        if (!"customer-client".equals(key)) {
            logger.error("Invalid key header: {}", key);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        logger.debug("Searching customers with name {} on page {} with size {}", name, page, size);

        // Create a Pageable object
        Pageable pageable = PageRequest.of(page - 1, size); // Page is zero-based

        // Prepare a set to hold all matching customers
        Set<Customer> allMatchingCustomers = new HashSet<>();

        // Search by first name or last name
        Page<Customer> customersByFirstName = customerRepository.findByFirstNameContainingIgnoreCase(name, pageable);
        Page<Customer> customersByLastName = customerRepository.findByLastNameContainingIgnoreCase(name, pageable);

        // Combine results to avoid duplicates
        allMatchingCustomers.addAll(customersByFirstName.getContent());
        allMatchingCustomers.addAll(customersByLastName.getContent());

        // If email is provided, filter results further
        if (email != null && !email.trim().isEmpty()) {
            logger.debug("Filtering results by email {}", email);
            allMatchingCustomers = allMatchingCustomers.stream()
                    .filter(customer -> customer.getEmail().equalsIgnoreCase(email))
                    .collect(Collectors.toSet());
        }

        // Create a paginated response based on combined results
        List<Customer> paginatedCustomers = new ArrayList<>(allMatchingCustomers);

        if (paginatedCustomers.isEmpty()) {
            logger.warn("No customers found with name {} and email {}", name, email);
            return ResponseEntity.status(HttpStatus.OK).body(Collections.emptyList());
        }

        logger.debug("Found {} customers matching the name {} and email {}", paginatedCustomers.size(), name, email);
        return ResponseEntity.ok(paginatedCustomers);
    }


}
