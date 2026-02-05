package com.ajh.job.adapter.in.web;

import com.ajh.job.adapter.in.web.dto.JobRequest;
import com.ajh.job.application.service.JobService;
import com.ajh.job.domain.model.JobOrder;
import com.ajh.job.domain.model.JobMatch;
import com.ajh.candidate.infrastructure.persistence.entity.CandidateEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 📡 NEURAL GATEWAY CONTROLLER
 * Centraliza a inteligência de colisão entre Vagas e Candidatos.
 */
@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") 
public class JobController {

    private final JobService jobService;

    /**
     * 🟢 CREATE: Ingestão de nova oportunidade estratégica.
     */
    @PostMapping
    public ResponseEntity<JobOrder> createJob(@RequestBody JobRequest request) {
        log.info(">>> [API] Ingesting mission context: {}", request.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(jobService.createJob(mapToDomain(request, null)));
    }

    /**
     * 🛠️ UPDATE: Recalibragem de contexto existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<JobOrder> updateJob(@PathVariable Long id, @RequestBody JobRequest request) {
        log.info(">>> [API] Recalibrating mission context ID: {}", id);
        return ResponseEntity.ok(jobService.createJob(mapToDomain(request, id)));
    }

    /**
     * 📚 LIST: Popula o seletor de missões no Dashboard.
     */
    @GetMapping
    public ResponseEntity<List<JobOrder>> getAllJobs() {
        log.debug(">>> [API] Fetching all active decision contexts.");
        return ResponseEntity.ok(jobService.listAllJobs());
    }

    /**
     * 📊 RANKING DINÂMICO: O motor principal do Dashboard.
     * ⚠️ IMPORTANTE: Este é o endpoint que o Frontend DEVE chamar ao trocar a vaga.
     * Fluxo: Recalcula via IA -> Flush no Banco -> Gera Ranking Arena -> Devolve Match Completo.
     */
    @GetMapping("/{id}/ranking")
    public ResponseEntity<List<JobMatch>> getJobRanking(@PathVariable Long id, Authentication authentication) {
        Long userId = extractUserId(authentication);
        log.info("📡 [API] Initiating Dynamic Neural Ranking | Job ID: {} | User: {}", id, userId);
        
        // Esta chamada no Service agora garante a sincronia total com o Python
        List<JobMatch> rankings = jobService.calculateRanking(id, userId);
        
        log.info("✅ [API] Ranking delivery complete. Nodes found: {}", rankings.size());
        return ResponseEntity.ok(rankings);
    }

    /**
     * 🧠 MAPPER: Conversão DTO -> Domain.
     */
    private JobOrder mapToDomain(JobRequest request, Long id) {
        return JobOrder.builder()
                .id(id)
                .title(request.getTitle() != null ? request.getTitle() : "Dynamic Mission Alpha")
                .description(request.getDescription())
                .company(request.getCompany() != null ? request.getCompany() : "Global Intelligence")
                .salaryRange(request.getSalaryRange())
                .location(request.getLocation() != null ? request.getLocation() : "Remote / Global")
                .status("OPEN")
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Long extractUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof CandidateEntity user) {
            return user.getId();
        }
        // Fallback seguro para o cluster de desenvolvimento
        return 1L; 
    }
}