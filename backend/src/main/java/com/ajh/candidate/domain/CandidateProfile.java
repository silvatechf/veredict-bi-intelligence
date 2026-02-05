package com.ajh.candidate.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidade de Domínio Soberana para o Perfil do Candidato.
 * Blindada para processamento via IA e integração com o MatchingEngine.
 */
@Getter 
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProfile {
    private Long id;
    private Long candidateId;
    private String name;
    private String role;
    private String seniority;
    
    // 🛡️ CAMPO CRÍTICO: Exigido pelo CandidateProfileMapper e MatchingService
    private String extractedText; 
    
    // 🧠 CAMPOS DE INTELIGÊNCIA: Achatados pelo Python para evitar o erro JSON START_OBJECT
    private String aiSummary;
    private String whyNotHundred;
    private String technicalGapInsight;
    private String seniorityInsight;
    private String contextDriftInsight;
    private String scalabilityInsight;

    // 📊 COLEÇÕES: Inicializadas via Builder para evitar NullPointerException
    @Builder.Default
    private List<String> skills = new ArrayList<>();
    
    @Builder.Default
    private List<String> interviewQuestions = new ArrayList<>();
}