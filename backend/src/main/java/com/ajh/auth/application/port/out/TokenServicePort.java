package com.ajh.auth.application.port.out;

import org.springframework.security.core.userdetails.UserDetails;

public interface TokenServicePort {
    // Agora aceita UserDetails, tornando-o compatível com Candidate
    String generateToken(UserDetails userDetails);
    String extractUsername(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
}