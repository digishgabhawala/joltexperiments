package com.drg.joltexperiments.bff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceConfigRepository extends JpaRepository<ServiceConfigEntity, Long> {
    Optional<ServiceConfigEntity> findByPathAndMethod(String path, String method);

}