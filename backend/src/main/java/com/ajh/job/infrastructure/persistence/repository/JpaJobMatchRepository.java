package com.ajh.job.infrastructure.persistence.repository;

import com.ajh.job.infrastructure.persistence.entity.JobMatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaJobMatchRepository extends JpaRepository<JobMatchEntity, Long> {

    /**
     * Recurso de Alto Nível: Busca um match específico para evitar re-processamento por IA.
     */
    Optional<JobMatchEntity> findByJobIdAndCandidateId(Long jobId, Long candidateId);

    /**
     * ✅ FIX: Busca todos os matches de um candidato específico.
     * Necessário para o MatchMakerService processar o histórico do talento.
     */
    List<JobMatchEntity> findAllByCandidateId(Long candidateId);

    /**
     * 🛡️ HIGIENE DE CONTEXTO: Remove snapshots obsoletos de uma vaga.
     * @Modifying é obrigatório para operações de DELETE/UPDATE.
     */
    @Modifying
    @Transactional
    void deleteByJobId(Long jobId);
}