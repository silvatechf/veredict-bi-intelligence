package com.ajh.auth.application.port.in.web;

import com.ajh.auth.application.port.in.AuthUseCase;
import com.ajh.auth.application.port.in.LoginCommand;
import com.ajh.auth.application.port.in.RegisterCommand;
import com.ajh.auth.domain.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthUseCase authUseCase;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginCommand command) {
        try {
            log.info("Receiving login request for: {}", command.getEmail());
            return ResponseEntity.ok(authUseCase.login(command));
        } catch (Exception e) {
            log.error("Login Controller Error: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterCommand command) {
        return ResponseEntity.ok(authUseCase.register(command));
    }
}