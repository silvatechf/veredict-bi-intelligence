package com.ajh.candidate.application.service;

import com.ajh.candidate.adapter.out.ai.AiEngineClient;
import com.ajh.candidate.application.port.in.CandidateUseCase;
import com.ajh.candidate.application.port.out.CandidateRepositoryPort;
import com.ajh.candidate.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*; 
import java.util.stream.Collectors;

@Slf4j 
@Service
@RequiredArgsConstructor
public class CandidateService implements CandidateUseCase {

    private final AiEngineClient aiEngineClient;
    private final CandidateRepositoryPort candidateRepository;

    @Override 
    @Transactional
    public CandidateProfileDto uploadAndAnalyze(MultipartFile file, String email) {
        log.info("📊 [NEURAL-AUDIT] Initiating ingestion for: {}", email);
        
        String rawText = extractText(file); 
        String naturalIdentity = generateHash(rawText);
        
        Candidate candidate = candidateRepository.findByNaturalIdentity(naturalIdentity)
                .orElseGet(() -> {
                    Candidate newCandidate = new Candidate();
                    newCandidate.setNaturalIdentity(naturalIdentity);
                    return newCandidate;
                });

        // 🧠 Ingestão via Llama-3.3-70B (Alta Precisão)
        CandidateProfileDto aiResult = aiEngineClient.analyze(file);
        
        if (isAiPayloadInvalid(aiResult)) {
            log.error("❌ AI Payload Corrupted or Rate Limited for: {}", email);
            candidate.setAssessmentStatus("INVALID_AI_RESPONSE");
        } else {
            syncStableData(candidate, aiResult, naturalIdentity, email);
            candidate.setAssessmentStatus("ANALYZED");
            log.info("✅ [NEURAL-SUCCESS] Handshake complete for: {}", email);
        }

        Candidate savedCandidate = candidateRepository.saveAndFlush(candidate);
        return mapToDto(savedCandidate);
    }

    /**
     * 🎯 RECALCULATION ENGINE (O segredo do Dashboard Dinâmico)
     * Recalcula o Match de todos os candidatos contra a nova Job Description.
     */
    @Override
    @Transactional
    public void recalculateAllMatches(String jd) {
        log.info("🎯 [NEURAL-RECALIBRATION] Initiating global match update for JD Briefing.");
        
        List<Candidate> candidates = candidateRepository.findByRoleAndAssessmentStatus(Role.CANDIDATE, "ANALYZED");

        if (candidates.isEmpty()) {
            log.warn("⚠️ No analyzed candidates found for recalibration.");
            return;
        }

        for (Candidate candidate : candidates) {
            try {
                log.info("🔄 Recalibrating node: {}", candidate.getName());

                // 🛡️ ANTI-RATE LIMIT: Delay tático para não estourar a cota do Groq
                Thread.sleep(400); 

                // 🚀 Uso do modelo leve (8B) via Client para performance
                CandidateProfileDto recalibrated = aiEngineClient.recalculate(
                    candidate.getAiSummary(), 
                    jd
                );

                candidate.setMatchScore(recalibrated.getMatchScore());
                candidate.setTechnicalFit(recalibrated.getTechnicalFit());
                candidate.setContextFit(recalibrated.getContextFit());
                
                // Atualiza a justificativa neural que aparece no TopologyRadar.tsx
                if (recalibrated.getWhyNotHundred() != null) {
                    candidate.setWhyNotHundred(recalibrated.getWhyNotHundred());
                }

            } catch (Exception e) {
                log.error("❌ Failed to recalibrate candidate {}: {}", candidate.getName(), e.getMessage());
            }
        }
        
        // Persiste em lote para garantir sincronia do Banco com a UI
        candidateRepository.saveAll(candidates);
        log.info("✅ [RECALIBRATION-COMPLETE] All nodes synchronized.");
    }

    private void syncStableData(Candidate c, CandidateProfileDto dto, String hash, String email) {
        c.setNaturalIdentity(hash);
        c.setEmail(email); 
        c.setRole(Role.CANDIDATE); 
        c.setName(dto.getName() != null ? dto.getName().trim() : "Verified Node " + hash.substring(0,6));
        c.setSkills(dto.getSkills() != null ? new ArrayList<>(dto.getSkills()) : new ArrayList<>());
        
        c.setMatchScore(dto.getMatchScore());
        c.setTechnicalFit(dto.getTechnicalFit());
        c.setContextFit(dto.getContextFit());
        
        c.setTechnicalGapRisk(dto.getTechnicalGapRisk());
        c.setSeniorityMismatchRisk(dto.getSeniorityMismatchRisk());
        c.setContextDriftRisk(dto.getContextDriftRisk());
        c.setScalabilityRisk(dto.getScalabilityRisk());
        
        c.setTechnicalGapInsight(dto.getTechnicalGapInsight());
        c.setSeniorityInsight(dto.getSeniorityInsight());
        c.setContextDriftInsight(dto.getContextDriftInsight());
        c.setScalabilityInsight(dto.getScalabilityInsight());
        
        c.setAiSummary(dto.getAiSummary());
        c.setSeniority(dto.getSeniority());
        c.setInterviewQuestionsFromList(dto.getInterviewQuestions());
        c.setWhyNotHundred(dto.getWhyNotHundred());
        
        if (c.getPassword() == null) c.setPassword("{noop}pass2026");
    }

    private CandidateProfileDto mapToDto(Candidate c) {
        // Limpeza de perguntas de entrevista para o JSON não quebrar
        List<String> questions = new ArrayList<>();
        if (c.getInterviewQuestions() != null && !c.getInterviewQuestions().isBlank()) {
            questions = Arrays.stream(c.getInterviewQuestions().split("\n"))
                    .filter(s -> !s.isBlank())
                    .collect(Collectors.toList());
        }

        return CandidateProfileDto.builder()
                .id(c.getId())
                .name(c.getName())
                .matchScore(c.getMatchScore())
                .technicalFit(c.getTechnicalFit())
                .contextFit(c.getContextFit())
                .technicalGapRisk(c.getTechnicalGapRisk())
                .seniorityMismatchRisk(c.getSeniorityMismatchRisk())
                .contextDriftRisk(c.getContextDriftRisk())
                .scalabilityRisk(c.getScalabilityRisk())
                .technicalGapInsight(c.getTechnicalGapInsight())
                .seniorityInsight(c.getSeniorityInsight())
                .contextDriftInsight(c.getContextDriftInsight())
                .scalabilityInsight(c.getScalabilityInsight())
                .aiSummary(c.getAiSummary())
                .skills(c.getSkills())
                .assessmentStatus(c.getAssessmentStatus())
                .whyNotHundred(c.getWhyNotHundred())
                .interviewQuestions(questions) 
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private boolean isAiPayloadInvalid(CandidateProfileDto d) { 
        return d == null || d.getName() == null || d.getName().contains("Audit Failure") || d.getName().contains("Limit Reached"); 
    }

    private String extractText(MultipartFile f) {
        try (PDDocument doc = Loader.loadPDF(f.getBytes())) { 
            return new PDFTextStripper().getText(doc); 
        } catch (Exception e) { 
            log.error("❌ PDF Extraction Error: {}", e.getMessage());
            return "Extraction Error"; 
        }
    }

    private String generateHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { return UUID.randomUUID().toString(); }
    }

    @Override 
    @Transactional(readOnly = true) 
    public List<CandidateProfileDto> getRankedCandidates() {
        return candidateRepository.findByRoleAndAssessmentStatus(Role.CANDIDATE, "ANALYZED")
                .stream()
                .map(this::mapToDto)
                .sorted((a,b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .toList();
    }

    @Override @Transactional public void deleteCandidate(Long id) { candidateRepository.deleteById(id); }
    @Override @Transactional(readOnly = true) public CandidateProfileDto getProfileByEmail(String e) { return candidateRepository.findByEmail(e).map(this::mapToDto).orElseThrow(); }
    @Override @Transactional(readOnly = true) public CandidateProfileDto performDeepAnalysis(Long id) { return candidateRepository.findById(id).map(this::mapToDto).orElseThrow(); }
}