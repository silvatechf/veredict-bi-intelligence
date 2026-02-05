package com.ajh.matching.infrastructure.persistence.adapter;

import com.ajh.matching.application.port.out.MatchRepositoryPort;
import com.ajh.matching.domain.MatchResult;
import com.ajh.matching.infrastructure.persistence.entity.MatchResultEntity;
import com.ajh.matching.infrastructure.persistence.repository.JpaMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MatchRepositoryAdapter implements MatchRepositoryPort {

    private final JpaMatchRepository jpaRepository;

    @Override
    public MatchResult save(MatchResult matchResult) {
        MatchResultEntity entity = mapToEntity(matchResult);
        MatchResultEntity saved = jpaRepository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public List<MatchResult> findByCandidateId(Long candidateId) {
        return jpaRepository.findByCandidateIdOrderByCreatedAtDesc(candidateId)
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    /**
     * 🎯 IMPLEMENTAÇÃO OBRIGATÓRIA: Destrava o compilador e o Dashboard
     */
    @Override
    public List<MatchResult> findByJobId(Long jobId) {
        return jpaRepository.findByJobIdOrderByMatchScoreDesc(jobId)
                .stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    // --- Mappers Blindados: Agora transportando a Matrix de Risco ---

    private MatchResultEntity mapToEntity(MatchResult domain) {
        return MatchResultEntity.builder()
                .candidateId(domain.getCandidateId())
                .jobId(domain.getJobId())
                .matchScore(domain.getMatchScore())
                .fitJustification(domain.getFitJustification())
                .missingSkills(domain.getMissingSkills())
                // 📊 Persistindo os 4 Pilares de Risco
                .technicalGapRisk(domain.getTechnicalGapRisk())
                .seniorityMismatchRisk(domain.getSeniorityMismatchRisk())
                .contextDriftRisk(domain.getContextDriftRisk())
                .scalabilityRisk(domain.getScalabilityRisk())
                // 💡 Persistindo os Insights
                .technicalGapInsight(domain.getTechnicalGapInsight())
                .seniorityInsight(domain.getSeniorityInsight())
                .contextDriftInsight(domain.getContextDriftInsight())
                .scalabilityInsight(domain.getScalabilityInsight())
                .createdAt(domain.getCreatedAt())
                .build();
    }

    private MatchResult mapToDomain(MatchResultEntity entity) {
        return MatchResult.builder()
                .id(entity.getId())
                .candidateId(entity.getCandidateId())
                .jobId(entity.getJobId())
                .matchScore(entity.getMatchScore())
                .fitJustification(entity.getFitJustification())
                .missingSkills(entity.getMissingSkills())
                // 📊 Recuperando os 4 Pilares de Risco
                .technicalGapRisk(entity.getTechnicalGapRisk())
                .seniorityMismatchRisk(entity.getSeniorityMismatchRisk())
                .contextDriftRisk(entity.getContextDriftRisk())
                .scalabilityRisk(entity.getScalabilityRisk())
                // 💡 Recuperando os Insights
                .technicalGapInsight(entity.getTechnicalGapInsight())
                .seniorityInsight(entity.getSeniorityInsight())
                .contextDriftInsight(entity.getContextDriftInsight())
                .scalabilityInsight(entity.getScalabilityInsight())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}