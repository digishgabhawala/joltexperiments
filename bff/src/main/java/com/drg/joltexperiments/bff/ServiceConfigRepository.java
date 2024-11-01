package com.drg.joltexperiments.bff;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceConfigRepository extends JpaRepository<ServiceConfigEntity, Long> {
    Optional<ServiceConfigEntity> findByPathAndMethod(String path, String method);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE SERVICE_CONFIG RESTART IDENTITY", nativeQuery = true)
    void truncate();

}