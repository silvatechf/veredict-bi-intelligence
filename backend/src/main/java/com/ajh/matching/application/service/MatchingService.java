package com.ajh.matching.application.service;

import com.ajh.candidate.application.port.out.CandidateRepositoryPort;
import com.ajh.job.application.port.out.JobRepositoryPort;
import com.ajh.candidate.infrastructure.client.GroqClient;
import com.ajh.matching.application.port.out.MatchRepositoryPort;
import com.ajh.matching.domain.MatchResult;
import com.ajh.matching.domain.MatchingRankResponse;
import com.ajh.candidate.domain.MatchResultDto;
import com.ajh.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MatchingService {

    private final CandidateRepositoryPort candidateRepository;
    private final JobRepositoryPort jobRepository;
    private final GroqClient groqClient;
    private final MatchRepositoryPort matchRepository;

    /**
     * 🎯 SYNC ARENA
     * Recupera resultados persistidos. Se não houver nada, o dashboard exibe "Empty".
     */
    @Transactional(readOnly = true)
    public List<MatchResult> getRankedMatchesForJob(Long jobId) {
        log.info("📊 [SERVICE] Neural Arena Sync requested for Job ID: {}", jobId);
        return matchRepository.findByJobId(jobId);
    }

    /**
     * 🧠 PERFORM MATCH (Com Proteção de Idempotência)
     */
    @Transactional
    public MatchResult performMatch(Long candidateId, Long jobId) {
        // 🛡️ TRAVA TÁTICA: Evita chamadas duplicadas à IA se o match já existe
        List<MatchResult> history = matchRepository.findByCandidateId(candidateId);
        Optional<MatchResult> existingMatch = history.stream()
                .filter(m -> m.getJobId().equals(jobId))
                .findFirst();

        if (existingMatch.isPresent()) {
            log.info("♻️ [SERVICE] Asset already analyzed for Candidate {} and Job {}. Returning cached result.", candidateId, jobId);
            return existingMatch.get();
        }

        // Validações de Domínio
        var candidate = candidateRepository.findById(candidateId)
            .orElseThrow(() -> new ResourceNotFoundException("Candidate not found: " + candidateId));

        var profile = candidateRepository.findProfileByCandidateId(candidateId)
            .orElseThrow(() -> new ResourceNotFoundException("AI Profile missing for Candidate: " + candidateId));
            
        var job = jobRepository.findById(jobId)
            .orElseThrow(() -> new ResourceNotFoundException("Job node not found: " + jobId));

        log.info("🚀 [SERVICE] Initiating Neural Collision: [{}] vs [{}]", candidate.getName(), job.getTitle());

        // Chamada Groq IA
        MatchResultDto aiAnalysis = groqClient.getAiAnalysis(profile.getExtractedText(), job.getDescription());

        // Mapeamento Blindado v11.5
        MatchResult matchResult = MatchResult.builder()
                .candidateId(candidateId)
                .jobId(jobId)
                .matchScore(aiAnalysis.getMatchScore())
                .fitJustification(aiAnalysis.getFitJustification())
                .missingSkills(aiAnalysis.getMissingSkills())
                
                // 📊 MATRIZ DE RISCO (Anti-9.9%)
                .technicalGapRisk(aiAnalysis.getTechnicalGapRisk())
                .seniorityMismatchRisk(aiAnalysis.getSeniorityMismatchRisk())
                .contextDriftRisk(aiAnalysis.getContextDriftRisk())
                .scalabilityRisk(aiAnalysis.getScalabilityRisk())
                
                // 💡 INSIGHTS TÁTICOS
                .technicalGapInsight(aiAnalysis.getTechnicalGapInsight())
                .seniorityInsight(aiAnalysis.getSeniorityInsight())
                .contextDriftInsight(aiAnalysis.getContextDriftInsight())
                .scalabilityInsight(aiAnalysis.getScalabilityInsight())
                
                .createdAt(LocalDateTime.now())
                .build();

        log.info("✅ [SERVICE] Analysis completed. Score: {}%", matchResult.getMatchScore());
        return matchRepository.save(matchResult);
    }

    /**
     * 📈 BATCH RANKING (Com Cláusula de Guarda)
     */
    @Transactional
    public MatchingRankResponse rankJobsForCandidate(Long candidateId, List<Long> jobIds) {
        if (jobIds == null || jobIds.isEmpty()) {
            log.warn("⚠️ [SERVICE] No target jobs provided for ranking.");
            return MatchingRankResponse.builder().candidateId(candidateId).totalJobsAnalyzed(0).rankings(List.of()).build();
        }

        log.info("📊 [SERVICE] Ranking Candidate {} against {} job nodes", candidateId, jobIds.size());

        List<MatchResult> rankings = jobIds.stream()
                .map(jobId -> {
                    try {
                        return this.performMatch(candidateId, jobId);
                    } catch (Exception e) {
                        log.error("❌ [SERVICE] Failure at Job Node {} - Reason: {}", jobId, e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted((m1, m2) -> Double.compare(m2.getMatchScore(), m1.getMatchScore()))
                .toList();

        return MatchingRankResponse.builder()
                .candidateId(candidateId)
                .totalJobsAnalyzed(rankings.size())
                .rankings(rankings)
                .build();
    }

    @Transactional(readOnly = true)
    public List<MatchResult> getHistoryByCandidate(Long candidateId) {
        return matchRepository.findByCandidateId(candidateId);
    }
}