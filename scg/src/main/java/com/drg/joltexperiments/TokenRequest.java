package com.drg.joltexperiments;

public class TokenRequest {
    private String authId;

    public TokenRequest(String authId) {
        this.authId = authId;
    }

    public String getAuthId() {
        return authId;
    }

    public void setAuthId(String authId) {
        this.authId = authId;
    }
    // Getters and setters
}
