package com.ajh.job.application.service;

import com.ajh.job.application.port.out.JobRepositoryPort;
import com.ajh.job.domain.model.JobOrder;
import com.ajh.job.domain.model.JobMatch;
import com.ajh.candidate.application.port.in.CandidateUseCase;
import com.ajh.candidate.application.port.out.CandidateRepositoryPort; // Importante para o Flush
import com.ajh.candidate.application.service.MatchMakerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Isolation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepositoryPort jobRepository;
    private final JobAiAnalysisService aiAnalysisService;
    private final MatchMakerService matchMakerService;
    private final CandidateUseCase candidateUseCase;
    private final CandidateRepositoryPort candidateRepository; // Injeção necessária para sincronia

    @Transactional(readOnly = true)
    public Optional<JobOrder> getJobById(Long id) {
        log.debug(">>> [JOB-SERVICE] Fetching context for Job ID: {}", id);
        return jobRepository.findById(id);
    }

    @Transactional
    public JobOrder createJob(JobOrder jobOrder) {
        log.info("🚀 [JOB-SERVICE] Initializing neural extraction for: {}", jobOrder.getTitle());

        if (jobOrder.getCreatedAt() == null) {
            jobOrder.setCreatedAt(LocalDateTime.now());
        }

        if (jobOrder.getDescription() != null && jobOrder.getDescription().length() > 20) {
            List<String> requirements = aiAnalysisService.extractRequirements(jobOrder.getDescription());
            String seniority = aiAnalysisService.estimateSeniority(jobOrder.getDescription());
            
            jobOrder.setRequirements(requirements);
            jobOrder.setSeniority(seniority != null ? seniority.toUpperCase() : "NOT_SPECIFIED");
        } else {
            log.warn("⚠️ [JOB-SERVICE] Weak description for Job: {}. AI analysis skipped.", jobOrder.getTitle());
        }

        return jobRepository.save(jobOrder);
    }

    @Transactional(readOnly = true)
    public List<JobOrder> listAllJobs() {
        return jobRepository.findAll().stream()
                .sorted(Comparator.comparing(JobOrder::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    /**
     * 📊 RANKING NEURAL DINÂMICO
     * Este método garante que, ao trocar de vaga no Front, a Arena seja recalibrada.
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public synchronized List<JobMatch> calculateRanking(Long jobId, Long userId) {
        log.info("📊 [JOB-SERVICE] Neural ranking cycle initiated | Job: {} | User: {}", jobId, userId);
        
        JobOrder jobOrder = jobRepository.findById(jobId)
                .orElseThrow(() -> {
                    log.error("❌ [JOB-SERVICE] Job ID {} not found.", jobId);
                    return new RuntimeException("Job context missing.");
                });

        // 🚨 PASSO 1: Disparar recálculo neural (Python) baseado na descrição DESTA vaga específica
        log.info("🔄 [JOB-SERVICE] Recalibrating all candidates for JD: {}", jobOrder.getTitle());
        candidateUseCase.recalculateAllMatches(jobOrder.getDescription());

        // 🚨 PASSO 2: HARD FLUSH
        // Forçamos o Hibernate a persistir os scores novos AGORA. 
        // Sem isso, o próximo passo (rankCandidates) pode ler os scores antigos da vaga anterior.
        candidateRepository.flush();

        // 🚨 PASSO 3: Geração do Ranking na Arena
        // O MatchMaker agora lê os candidatos com a "verdade" atualizada para este JobId.
        List<JobMatch> rankings = matchMakerService.rankCandidates(jobOrder, userId);
        
        log.info("✅ [JOB-SERVICE] Ranking synchronized for {}. Nodes in Arena: {}", 
                 jobOrder.getTitle(), rankings.size());
        
        return rankings;
    }
}