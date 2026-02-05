package com.ajh.candidate.application.service;

import com.ajh.candidate.domain.Role;
import com.ajh.candidate.infrastructure.persistence.entity.CandidateEntity;
import com.ajh.candidate.infrastructure.persistence.repository.JpaCandidateRepository;
import com.ajh.job.domain.model.JobMatch;
import com.ajh.job.domain.model.JobOrder;
import com.ajh.job.infrastructure.persistence.entity.JobMatchEntity;
import com.ajh.job.infrastructure.persistence.repository.JpaJobMatchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MatchMakerService {

    private final JpaCandidateRepository candidateRepository;
    private final JpaJobMatchRepository jobMatchRepository;

    /**
     * 🧠 ORQUESTRAÇÃO DE RANKING NEURAL
     * Este método é chamado pelo JobService APÓS o recalculateAllMatches.
     */
    @Transactional
    public List<JobMatch> rankCandidates(JobOrder job, Long requestUserId) {
        log.info("📡 [MATCH-MAKER] Initiating neural collision for Job: {} (ID: {})", job.getTitle(), job.getId());
        
        // 🛡️ Filtro de Domínio: Busca apenas candidatos analisados
        List<CandidateEntity> eligibleCandidates = candidateRepository
                .findByRoleAndAssessmentStatus(Role.CANDIDATE, "ANALYZED");

        if (eligibleCandidates.isEmpty()) {
            log.warn("⚠️ [MATCH-MAKER] No eligible candidates found in cluster.");
            return Collections.emptyList();
        }

        // 🚀 Mapeia e Ordena: Os scores aqui já foram atualizados pelo CandidateService + IA
        List<JobMatch> rankings = eligibleCandidates.stream()
                .map(candidate -> mapToMatch(candidate, job))
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .collect(Collectors.toList());

        // 💾 PERSISTÊNCIA ATÔMICA: Limpa a arena antiga e registra a nova colisão
        persistRankings(job.getId(), rankings);

        return rankings;
    }

    @Transactional
    protected void persistRankings(Long jobId, List<JobMatch> rankings) {
        // 🔥 KILL SWITCH: Remove o histórico anterior para evitar dados "viciados" no Dashboard
        jobMatchRepository.deleteByJobId(jobId);
        jobMatchRepository.flush(); // Garante que o delete aconteça antes do insert

        List<JobMatchEntity> entities = rankings.stream()
            .map(m -> JobMatchEntity.builder()
                .jobId(jobId)
                .candidateId(m.getCandidateId())
                .matchScore(m.getMatchScore())
                .technicalFit(m.getTechnicalFit())
                .contextFit(m.getContextFit())
                .technicalGapRisk(m.getTechnicalGapRisk())
                .seniorityMismatchRisk(m.getSeniorityMismatchRisk())
                .contextDriftRisk(m.getContextDriftRisk())
                .scalabilityRisk(m.getScalabilityRisk())
                .technicalGapInsight(m.getTechnicalGapInsight())
                .seniorityInsight(m.getSeniorityInsight())
                .contextDriftInsight(m.getContextDriftInsight())
                .scalabilityInsight(m.getScalabilityInsight())
                .whyNotHundred(m.getWhyNotHundred())
                .build())
            .collect(Collectors.toList());
        
        jobMatchRepository.saveAll(entities);
        log.info("💾 [DATABASE] Neural Arena synchronized. {} nodes persisted for Job ID: {}.", entities.size(), jobId);
    }

    private JobMatch mapToMatch(CandidateEntity entity, JobOrder job) {
        // 🛡️ SCORE ENFORCEMENT: Se o score for nulo, usamos o piso do Python (0.1)
        double score = entity.getMatchScore() != null ? entity.getMatchScore() : 0.1;
        
        return JobMatch.builder()
                .jobId(job.getId())
                .candidateId(entity.getId())
                .candidateName(entity.getName())
                .candidateEmail(entity.getEmail())
                .matchScore(score)
                .technicalFit(Optional.ofNullable(entity.getTechnicalFit()).orElse(0.1))
                .contextFit(Optional.ofNullable(entity.getContextFit()).orElse(0.1))
                .skills(entity.getSkills()) 
                .role(entity.getProfessionalTarget() != null ? entity.getProfessionalTarget() : "General Talent")
                .seniority(entity.getSeniority())
                .aiSummary(entity.getAiSummary())
                .interviewQuestions(parseInterviewQuestions(entity.getInterviewQuestions()))
                // Radar Axes
                .technicalGapRisk(Optional.ofNullable(entity.getTechnicalGapRisk()).orElse(0.1))
                .seniorityMismatchRisk(Optional.ofNullable(entity.getSeniorityMismatchRisk()).orElse(0.1))
                .contextDriftRisk(Optional.ofNullable(entity.getContextDriftRisk()).orElse(0.1))
                .scalabilityRisk(Optional.ofNullable(entity.getScalabilityRisk()).orElse(0.1))
                // Insights Qualitativos (O que aparece no novo Card do Dashboard)
                .technicalGapInsight(entity.getTechnicalGapInsight())
                .seniorityInsight(entity.getSeniorityInsight())
                .contextDriftInsight(entity.getContextDriftInsight())
                .scalabilityInsight(entity.getScalabilityInsight())
                .whyNotHundred(entity.getWhyNotHundred())
                .createdAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt() : entity.getCreatedAt())
                .build();
    }

    private List<String> parseInterviewQuestions(String questionsStr) {
        if (questionsStr == null || questionsStr.isBlank()) return new ArrayList<>();
        return Arrays.stream(questionsStr.split("\\n|;|\\|"))
                .map(String::trim)
                .filter(s -> s.length() > 10)
                .distinct()
                .limit(5)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<JobMatch> getCandidateMatchHistory(Long candidateId) {
        return candidateRepository.findById(candidateId)
                .map(c -> List.of(mapToMatch(c, JobOrder.builder().id(0L).build())))
                .orElse(Collections.emptyList());
    }
}