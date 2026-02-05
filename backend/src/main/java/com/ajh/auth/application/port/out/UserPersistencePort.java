package com.ajh.auth.application.port.out;

import com.ajh.auth.domain.User;
import java.util.Optional;
import java.util.UUID;

public interface UserPersistencePort {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id); // O método que estava faltando!
}