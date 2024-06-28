package com.drg.joltexperiments.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Bean
    public ApplicationRunner initializer() {
        return args -> {
            User user = new User();
            user.setUsername("user");
            user.setEmail("user@example.com");
            user.setPassword(encoder.encode("password"));
            userRepository.save(user) ;
        };
    }

}
