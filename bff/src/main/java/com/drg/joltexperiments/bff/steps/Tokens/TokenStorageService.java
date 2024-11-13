package com.drg.joltexperiments.bff.steps.Tokens;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TokenStorageService {

    @Autowired
    private final TokenRepository tokenRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public TokenStorageService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        this.objectMapper = new ObjectMapper();
    }

    public void storeToken(String token, Object data) {
        try {
            // Convert data to JSON string for storage
            String jsonData = objectMapper.writeValueAsString(data);

            // Set expiration to 24 hours from now (example)
            LocalDateTime expirationTime = LocalDateTime.now().plusHours(24);

            TokenEntity tokenEntity = new TokenEntity(token, jsonData, expirationTime);
            tokenRepository.save(tokenEntity);
        } catch (Exception e) {
            throw new RuntimeException("Failed to store token data", e);
        }
    }

    public Optional<Object> retrieveTokenData(String token) {
        return tokenRepository.findByToken(token).map(tokenEntity -> {
            try {
                return objectMapper.readValue(tokenEntity.getData(), Object.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize token data", e);
            }
        });
    }

    public void deleteExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteByExpirationTimeBefore(now);
    }
}
