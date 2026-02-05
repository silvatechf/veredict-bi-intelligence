package com.ajh.auth.adapter.out.persistence;

import com.ajh.auth.application.port.out.UserPersistencePort;
import com.ajh.auth.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaUserPersistenceAdapter implements UserPersistencePort {

    private final UserJpaRepository repository;

    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return repository.findById(id); // O Spring Data já tem esse pronto no repository
    }
}