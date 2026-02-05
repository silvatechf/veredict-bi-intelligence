package com.ajh.candidate.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CandidateProfileDto {

    private Long id;
    private String name;
    private String role;
    private String seniority;
    
    private String nodeId;
    private String naturalIdentity;

    @JsonProperty("assessmentStatus")
    @JsonAlias({"assessment_status", "status", "assessmentStatus"})
    private String assessmentStatus;

    // --- 🎯 SCORES PRINCIPAIS (Escala 0-100) ---
    @JsonProperty("matchScore")
    @JsonAlias({"match_score", "score", "matchScore"})
    private Double matchScore;

    @JsonProperty("technicalFit")
    @JsonAlias({"technical_fit", "tech_fit", "technicalFit"})
    private Double technicalFit;

    @JsonProperty("contextFit")
    @JsonAlias({"context_fit", "context_score", "contextFit"})
    private Double contextFit;

    @JsonProperty("riskFactor")
    @JsonAlias({"risk_factor", "risk", "riskFactor"})
    private Double riskFactor;

    // --- 🚀 MATRIX DE RISCO (Contratos para Radar Recharts) ---
    @JsonProperty("technicalGapRisk")
    @JsonAlias({"technical_gap_risk", "tech_gap", "technicalGapRisk"})
    private Double technicalGapRisk;

    @JsonProperty("seniorityMismatchRisk")
    @JsonAlias({"seniority_mismatch_risk", "seniority_risk", "seniorityMismatchRisk"})
    private Double seniorityMismatchRisk;

    @JsonProperty("contextDriftRisk")
    @JsonAlias({"context_drift_risk", "drift_risk", "contextDriftRisk"})
    private Double contextDriftRisk;

    @JsonProperty("scalabilityRisk")
    @JsonAlias({"scalability_risk", "growth_risk", "scalabilityRisk"})
    private Double scalabilityRisk;

    // --- 💡 INSIGHTS ESTRATÉGICOS ---
    @JsonAlias({"technical_gap_insight", "technicalGapInsight"})
    private String technicalGapInsight;

    @JsonAlias({"seniority_insight", "seniorityInsight"})
    private String seniorityInsight;

    @JsonAlias({"context_drift_insight", "contextDriftInsight"})
    private String contextDriftInsight;

    @JsonAlias({"scalability_insight", "scalabilityInsight"})
    private String scalabilityInsight;

    // --- 🏛️ EXTRATOS E EVIDÊNCIAS ---
    @JsonAlias({"ai_summary", "aiSummary"})
    private String aiSummary;

    @JsonAlias({"interview_questions", "interviewQuestions"})
    private List<String> interviewQuestions;

    @JsonProperty("whyNotHundred")
    @JsonAlias({"why_not_hundred", "whyNot100"})
    private Object whyNotHundred; 

    // ✅ NOVO CAMPO: Resolve o erro de compilação no CandidateService
    @JsonProperty("justification")
    @JsonAlias({"justification", "match_justification", "reason"})
    private String justification;

    private List<String> skills;

    private List<Double> embedding;

    private LocalDateTime updatedAt;

    // --- 🛡️ BARREIRA SANITÁRIA ---

    public String getWhyNotHundred() {
        if (whyNotHundred == null) return "Verbatim evidence aligns with optimal profile.";
        if (whyNotHundred instanceof List) {
            return ((List<?>) whyNotHundred).stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(" | "));
        }
        return whyNotHundred.toString();
    }

    /**
     * ✅ RESOLUÇÃO DE ERRO: Garante que o service sempre tenha um fallback 
     * para o texto qualitativo, mesmo que o campo venha vazio.
     */
    public String getJustification() {
        if (justification != null && !justification.isBlank()) return justification;
        return getWhyNotHundred(); // Fallback inteligente
    }

    private Double ensureMin(Double val) {
        return (val == null || val <= 0.01) ? 0.1 : val;
    }

    public Double getMatchScore() { return ensureMin(matchScore); }
    public Double getTechnicalFit() { return ensureMin(technicalFit); }
    public Double getContextFit() { return ensureMin(contextFit); }
    public Double getRiskFactor() { return ensureMin(riskFactor); }
    
    public Double getTechnicalGapRisk() { return ensureMin(technicalGapRisk); }
    public Double getSeniorityMismatchRisk() { return ensureMin(seniorityMismatchRisk); }
    public Double getContextDriftRisk() { return ensureMin(contextDriftRisk); }
    public Double getScalabilityRisk() { return ensureMin(scalabilityRisk); }

    public List<String> getSkills() { 
        return skills != null ? skills : new ArrayList<>(); 
    }

    public List<String> getInterviewQuestions() { 
        return interviewQuestions != null ? interviewQuestions : new ArrayList<>(); 
    }
}