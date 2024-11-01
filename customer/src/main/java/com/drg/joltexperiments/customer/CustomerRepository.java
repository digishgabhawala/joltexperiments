package com.drg.joltexperiments.customer;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE CUSTOMER RESTART IDENTITY", nativeQuery = true)
    void truncate();

    Page<Customer> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);
    Page<Customer> findByLastNameContainingIgnoreCase(String lastName, Pageable pageable);

}
