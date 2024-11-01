package com.drg.joltexperiments.customer;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE CUSTOMER RESTART IDENTITY", nativeQuery = true)
    void truncate();
}
