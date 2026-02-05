package com.ajh.candidate.infrastructure.persistence.repository;

import com.ajh.candidate.domain.Role;
import com.ajh.candidate.infrastructure.persistence.entity.CandidateEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface JpaCandidateRepository extends JpaRepository<CandidateEntity, Long> {
    
    Optional<CandidateEntity> findByNaturalIdentity(String naturalIdentity);

    Optional<CandidateEntity> findByEmail(String email);

    // ✅ PERFORMANCE ELITE: Já estava correto, mantendo o EntityGraph
    @Override
    @EntityGraph(attributePaths = {"skills"})
    List<CandidateEntity> findAll();

    // 🛡️ FILTRO DE RANKING: Traz apenas CANDIDATE e ANALYZED
    // O EntityGraph aqui é vital para o ranking não travar em N+1
    @EntityGraph(attributePaths = {"skills"})
    List<CandidateEntity> findByRoleAndAssessmentStatus(Role role, String assessmentStatus);

    // 📊 CONTRA-GOLPE DE CONCORRÊNCIA: Conta rápido se há massa crítica
    long countByRoleAndAssessmentStatus(Role role, String assessmentStatus);
}