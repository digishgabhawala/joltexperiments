package com.drg.joltexperiments.bff.steps.Tokens;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, String> {
    Optional<TokenEntity> findByToken(String token);

    void deleteByExpirationTimeBefore(LocalDateTime now);
}
