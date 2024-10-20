package com.drg.joltexperiments.bff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceConfigRepository extends JpaRepository<ServiceConfigEntity, String> {
}