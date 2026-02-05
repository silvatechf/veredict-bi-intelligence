package com.ajh.job.domain.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobMatch {
    // 🆔 Identificador Soberano (Snapshot ID)
    private Long id; 
    private Long jobId;
    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
    
    // 📊 Scores Principais (Normalizados 0-100)
    private Double matchScore;
    private Double technicalFit;
    private Double contextFit;

    // ✅ LABELS TÉCNICAS (As badges do Dashboard)
    private List<String> skills;

    // 🔬 Neural Risk Audit (VETORES DE RISCO - Radar Chart)
    private Double technicalGapRisk;
    private Double seniorityMismatchRisk;
    private Double contextDriftRisk;
    private Double scalabilityRisk;

    // 📝 Neural Risk Audit (INSIGHTS TÉCNICOS DETALHADOS)
    private String technicalGapInsight;
    private String seniorityInsight;
    private String contextDriftInsight;
    private String scalabilityInsight;
    private String whyNotHundred;

    // 🤖 IA Strategic Context
    private String role;
    private String seniority;
    private String aiSummary;
    private List<String> interviewQuestions; // Roadmap de perguntas
    private String justification; // Exclusivo para a Arena de Comparação
    
    // ⏲️ Telemetria
    private LocalDateTime createdAt;
}