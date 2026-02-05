package com.ajh.candidate.infrastructure.persistence.repository;

import com.ajh.candidate.infrastructure.persistence.entity.CandidateProfileEntity; // IMPORTANTE: Use a Entity
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface JpaCandidateProfileRepository extends JpaRepository<CandidateProfileEntity, Long> {
    Optional<CandidateProfileEntity> findByCandidateId(Long candidateId);
}