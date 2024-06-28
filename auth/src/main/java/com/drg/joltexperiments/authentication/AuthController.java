package com.drg.joltexperiments.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        log.debug(user.toString());
        Long authId = authService.validate(user.getUsername(), user.getPassword());
        if (authId != null) {
            return ResponseEntity.ok(authId);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }
}
