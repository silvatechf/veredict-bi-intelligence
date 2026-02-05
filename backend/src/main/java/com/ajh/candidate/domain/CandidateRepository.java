package com.ajh.candidate.domain;

import com.ajh.candidate.infrastructure.persistence.entity.CandidateEntity; // IMPORTANTE: Importar a Entity
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateEntity, Long> {
    // Agora o Spring sabe que deve gerenciar a CandidateEntity no banco
    Optional<CandidateEntity> findByEmail(String email);
}