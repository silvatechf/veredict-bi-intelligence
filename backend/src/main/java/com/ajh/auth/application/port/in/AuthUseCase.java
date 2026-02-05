package com.ajh.auth.application.port.in;

import com.ajh.auth.domain.AuthResponse;

public interface AuthUseCase {
    AuthResponse login(LoginCommand command);
    AuthResponse register(RegisterCommand command);
}