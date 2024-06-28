package com.drg.joltexperiments.Token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Base64;
import java.util.Date;

public class JwtUtils {

;
    private static final String SECRET_KEY = Base64.getEncoder().encodeToString(
            Keys.secretKeyFor(SignatureAlgorithm.HS512).getEncoded()
    );


    public static String generateToken(String authId) {
        return Jwts.builder()
                .setSubject(authId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 864000000)) // 10 days
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public static String getAuthIdFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).getBody();
        return  claims.getSubject();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
