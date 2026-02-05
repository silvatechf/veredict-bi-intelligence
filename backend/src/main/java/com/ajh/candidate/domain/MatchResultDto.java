package com.ajh.candidate.domain;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultDto {
    // --- 🏛️ CAMPOS ORIGINAIS (Não remova, o TalentAnalysisService usa estes!) ---
    private Double matchScore;
    private Double technicalFit;   // Adicionado de volta
    private Double contextFit;     // Adicionado de volta
    private Double riskFactor;     // Adicionado de volta
    private String whyNotHundred;  // Adicionado de volta
    private String fitJustification;
    private List<String> missingSkills;

    // --- 📊 CAMPOS BI v10.0 (Para o Radar da Neural Arena) ---
    private Double technicalGapRisk;
    private Double seniorityMismatchRisk;
    private Double contextDriftRisk;
    private Double scalabilityRisk;

    // --- 💡 INSIGHTS TÁTICOS ---
    private String technicalGapInsight;
    private String seniorityInsight;
    private String contextDriftInsight;
    private String scalabilityInsight;
}