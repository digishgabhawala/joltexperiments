package com.drg.joltexperiments.Account;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Long> {



    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE ACCOUNT RESTART IDENTITY", nativeQuery = true)
    void truncate();

}
