package com.ajh.service;

import com.ajh.candidate.adapter.out.ai.AiEngineClient;
import com.ajh.candidate.application.port.out.CandidateRepositoryPort;
import com.ajh.candidate.domain.Candidate;
import com.ajh.candidate.domain.CandidateProfileDto;
import com.ajh.candidate.domain.MatchResultDto; 
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TalentAnalysisService {

    private final CandidateRepositoryPort candidateRepository;
    private final AiEngineClient aiEngineClient;

    /**
     * O Coração da Calibragem Neural.
     * Atualiza os scores comparando o perfil com uma vaga (JD) específica.
     */
    @Transactional
    public void updateMatchesBasedOnJD(List<CandidateProfileDto> candidates, String jd) {
        log.info(">>> [NEURAL ENGINE] Initiating re-scoring for {} nodes against new Target JD.", candidates.size());

        List<Candidate> updatedCandidates = new ArrayList<>();

        for (CandidateProfileDto dto : candidates) {
            try {
                String sanitizedSummary = neuralSanitizer(dto.getAiSummary());

                // 🚀 AJUSTE DE NOME: Alterado para 'recalculate' conforme definido no AiEngineClient
                // Se o seu AiEngineClient usa 'recalculateMatch', mantenha-o, mas os dois DEVEM ser iguais.
                CandidateProfileDto verdict = aiEngineClient.recalculate(sanitizedSummary, jd);

                candidateRepository.findById(dto.getId()).ifPresent(candidate -> {
                    // 1. Mapeamento de Scores com Null-Safe
                    candidate.setMatchScore(verdict.getMatchScore() != null ? verdict.getMatchScore() : 0.0);
                    candidate.setTechnicalFit(verdict.getTechnicalFit() != null ? verdict.getTechnicalFit() : 0.0);
                    candidate.setContextFit(verdict.getContextFit() != null ? verdict.getContextFit() : 0.0);
                    
                    // 2. RiskFactor e Justificativa
                    candidate.setRiskFactor(verdict.getRiskFactor() != null ? verdict.getRiskFactor() : 0.0); 
                    candidate.setWhyNotHundred(verdict.getWhyNotHundred() != null ? verdict.getWhyNotHundred() : "Analysis complete.");
                    candidate.setAssessmentStatus("CALIBRATED");
                    
                    updatedCandidates.add(candidate);
                });

                log.info(">>> [CALIBRATION SUCCESS] Node: {} | Score: {}%", dto.getName(), verdict.getMatchScore());
                
            } catch (Exception e) {
                log.error(">>> [CALIBRATION FAILED] Critical error on node {}: {}", dto.getName(), e.getMessage());
            }
        }
        
        // 🚀 OTIMIZAÇÃO: Salva todos de uma vez usando o Batch Size configurado no YML
        if (!updatedCandidates.isEmpty()) {
            candidateRepository.saveAll(updatedCandidates);
        }
        
        log.info(">>> [NEURAL ENGINE] Calibration sequence finished.");
    }

    private String neuralSanitizer(String summary) {
        if (summary == null) return "";
        return summary.replaceAll("(?i)(\\d{2,})\\s*(years|anos)\\s*(of experience|de experiência)", "extensive experience");
    }
}