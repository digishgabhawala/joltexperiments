package com.drg.joltexperiments.Token;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    @Test
    void generateToken() {
        String token = JwtUtils.generateToken("123");
        System.out.println(token);
        System.out.println(JwtUtils.validateToken(token));
        System.out.println(JwtUtils.getAuthIdFromToken(token));
    }

}