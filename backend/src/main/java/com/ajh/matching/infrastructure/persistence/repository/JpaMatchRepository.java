package com.ajh.matching.infrastructure.persistence.repository;

import com.ajh.matching.infrastructure.persistence.entity.MatchResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JpaMatchRepository extends JpaRepository<MatchResultEntity, Long> {
    
    List<MatchResultEntity> findByCandidateIdOrderByCreatedAtDesc(Long candidateId);

    /**
     * 🎯 ESSENCIAL: Busca os matches de uma vaga ordenados pelo maior score
     */
    List<MatchResultEntity> findByJobIdOrderByMatchScoreDesc(Long jobId);
}