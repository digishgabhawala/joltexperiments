package com.drg.joltexperiments.Token;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/token")
public class TokenController {

    @PostMapping("/generate")
    public String generateToken(@RequestBody AuthRequest authRequest) {
        return JwtUtils.generateToken(authRequest.getAuthId());
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestParam String token) {
        return JwtUtils.validateToken(token);
    }

    @GetMapping("/authId")
    public String getAuthIdFromToken(@RequestParam String token) {
        return JwtUtils.getAuthIdFromToken(token);
    }
}
