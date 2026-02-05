package com.ajh.candidate.adapter.in.web;

import com.ajh.candidate.application.port.in.CandidateUseCase;
import com.ajh.candidate.domain.CandidateProfileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 👤 CANDIDATE ASSET CONTROLLER
 * Responsável pela gestão do ciclo de vida dos ativos neurais (Candidatos).
 * O ranking dinâmico foi movido para o JobController para garantir sincronia de contexto.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/candidates")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowedHeaders = "*")
public class CandidateController {

    private final CandidateUseCase candidateUseCase;

    /**
     * 🟢 UPLOAD: Ingestão de novos candidatos.
     * Converte currículos físicos em DNA Neural via Python v4.2.
     * Preserva acentos (Félix) e gera identidades únicas.
     */
    @PostMapping("/upload")
    public ResponseEntity<CandidateProfileDto> uploadCv(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        if (file.isEmpty()) {
            log.warn(">>> [API WARN] Ingestion rejected: Payload is empty.");
            return ResponseEntity.badRequest().build();
        }

        String email;
        if (authentication != null && authentication.getName() != null) {
            email = authentication.getName();
        } else {
            // ✅ SANITIZAÇÃO: Gera e-mail fictício único para ingestão anônima no laboratório
            String baseName = file.getOriginalFilename() != null 
                ? file.getOriginalFilename().split("\\.")[0].replaceAll("[^a-zA-Z0-9]", "") 
                : "Talent";
            email = "ai_" + baseName + "_" + System.currentTimeMillis() + "@veredict.lab";
        }

        log.info(">>> [API] Starting neural ingestion for: {}", email);
        
        try {
            CandidateProfileDto result = candidateUseCase.uploadAndAnalyze(file, email);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error(">>> [API ERROR] Ingestion failed for {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 🧠 CALIBRATE (Batch): Recalibração forçada de toda a base.
     * Útil para testes de massa quando uma nova JD é definida.
     */
    @PostMapping("/recalculate")
    public ResponseEntity<Void> recalculateMatches(@RequestBody Map<String, String> payload) {
        String jobDescription = payload.get("jd");
        
        if (jobDescription == null || jobDescription.isBlank()) {
            log.warn(">>> [API WARN] Calibration skipped: No JD provided.");
            return ResponseEntity.badRequest().build();
        }

        log.info(">>> [API] Massive Neural Calibration initiated for JD context.");
        
        try {
            candidateUseCase.recalculateAllMatches(jobDescription);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(">>> [API ERROR] Global calibration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 🗑️ PURGE: Remoção física do nó candidato.
     * Limpa o banco para novos testes de ingestão.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidate(@PathVariable Long id) {
        log.info(">>> [API] Purging node ID: {}", id);
        try {
            candidateUseCase.deleteCandidate(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error(">>> [API ERROR] Purge failed for ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * 📚 LIST: Lista simples de candidatos para gestão.
     */
    @GetMapping
    public ResponseEntity<List<CandidateProfileDto>> listAll() {
        return ResponseEntity.ok(candidateUseCase.getRankedCandidates());
    }
}