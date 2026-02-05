package com.ajh.job.adapter.in.web;

import com.ajh.job.domain.model.JobMatch;
import com.ajh.candidate.application.service.MatchMakerService;
import com.ajh.job.application.service.JobService;
import com.ajh.job.domain.model.JobOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
// 🛡️ Ajustado para bater com a chamada do Frontend: talentService.getRankedMatches
@RequestMapping("/api/jobs") 
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class JobMatchController {

    private final MatchMakerService matchMakerService;
    private final JobService jobService;

    /**
     * 📊 RANKING NEURAL POR VAGA: 
     * Agora com tratamento de Optional para evitar Compilation Error.
     */
    @GetMapping("/{jobId}/ranking")
    public ResponseEntity<List<JobMatch>> getRankedMatchesForJob(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "1") Long userId) {
        
        log.info("📊 [BI-ARENA] Accessing Neural Arena | Job Context: {} | Requester: {}", jobId, userId);
        
        // 🛡️ TRATAMENTO DE OPTIONAL: Extrai a JobOrder ou lança 404 se não existir
        JobOrder job = jobService.getJobById(jobId)
                .orElseThrow(() -> {
                    log.error("❌ [BI-ARENA] Fatal: Job ID {} is missing from database.", jobId);
                    return new RuntimeException("Decision context (Job) not found.");
                });
        
        // 🧠 Processa a colisão entre a Vaga e os Candidatos analisados
        try {
            List<JobMatch> rankings = matchMakerService.rankCandidates(job, userId);
            return ResponseEntity.ok(rankings);
        } catch (Exception e) {
            log.error("❌ [BI-ARENA] Neural Processing Failure: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 🕵️ HISTÓRICO DO CANDIDATO: 
     * Retorna dossiês persistidos sem acionar o motor Python novamente.
     */
    @GetMapping("/history/candidate/{candidateId}")
    public ResponseEntity<List<JobMatch>> getCandidateHistory(@PathVariable Long candidateId) {
        log.info("🕵️ [BI-HISTORY] Retrieving Neural History for Node: {}", candidateId);
        return ResponseEntity.ok(matchMakerService.getCandidateMatchHistory(candidateId));
    }
}