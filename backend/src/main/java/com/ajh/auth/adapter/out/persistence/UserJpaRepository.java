package com.ajh.auth.adapter.out.persistence;

import com.ajh.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserJpaRepository extends JpaRepository<User, UUID> {
    
    /**
     * Busca um usuário pelo e-mail. 
     * Usado na autenticação e validação de registro.
     */
    Optional<User> findByEmail(String email);
}