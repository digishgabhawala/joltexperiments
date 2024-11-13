package com.drg.joltexperiments.bff.steps.Tokens;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_storage")
public class TokenEntity {

    @Id
    private String token;

    @Lob
    private String data;  // Store as JSON string or serialized string

    private LocalDateTime expirationTime;

    public TokenEntity() {}

    public TokenEntity(String token, String data, LocalDateTime expirationTime) {
        this.token = token;
        this.data = data;
        this.expirationTime = expirationTime;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public LocalDateTime getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(LocalDateTime expirationTime) {
        this.expirationTime = expirationTime;
    }
}
