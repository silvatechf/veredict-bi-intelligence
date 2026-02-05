package com.ajh.matching.adapter.in.web;

import com.ajh.matching.application.service.MatchingService;
import com.ajh.matching.domain.MatchResult;
import com.ajh.matching.domain.MatchingRankResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/matches") // ⚡ ROTA SOBERANA: Bate com o Next.js
@RequiredArgsConstructor
@Validated
public class MatchingController {

    private final MatchingService matchingService;

    /**
     * 📊 SYNC ARENA: O que o Dashboard Next.js chama ao carregar
     */
    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<MatchResult>> getRankedMatchesForJob(
            @PathVariable @NotNull @Min(1) Long jobId,
            @RequestParam(defaultValue = "1") Long userId) {
        
        log.info("📡 [API] Syncing Neural Arena | Job: {} | User: {}", jobId, userId);
        List<MatchResult> results = matchingService.getRankedMatchesForJob(jobId);
        
        return results.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(results);
    }

    /**
     * 🚀 ANALYZE: Dispara a colisão neural individual
     */
    @PostMapping("/analyze")
    public ResponseEntity<MatchResult> analyzeMatch(
            @RequestParam @NotNull @Min(1) Long candidateId,
            @RequestParam @NotNull @Min(1) Long jobId) {
        
        log.info("📥 [API] Neural Collision Request: Candidate {} vs Job {}", candidateId, jobId);
        MatchResult result = matchingService.performMatch(candidateId, jobId);
        return ResponseEntity.ok(result);
    }

    /**
     * 📈 RANK: Analisa um candidato contra várias vagas
     */
    @PostMapping("/rank")
    public ResponseEntity<MatchingRankResponse> rankJobs(
            @RequestParam @NotNull @Min(1) Long candidateId,
            @RequestBody @NotEmpty List<Long> jobIds) {
        
        log.info("📥 [API] Batch Ranking for Candidate {}", candidateId);
        MatchingRankResponse response = matchingService.rankJobsForCandidate(candidateId, jobIds);
        return ResponseEntity.ok(response);
    }
}