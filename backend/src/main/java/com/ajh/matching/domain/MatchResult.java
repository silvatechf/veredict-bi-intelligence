package com.ajh.matching.domain;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResult {
    private Long id;
    private Long candidateId;
    private Long jobId;
    private Double matchScore;
    private String fitJustification;
    private List<String> missingSkills;
    
    // Matriz de Risco
    private Double technicalGapRisk;
    private Double seniorityMismatchRisk;
    private Double contextDriftRisk;
    private Double scalabilityRisk;

    // Insights
    private String technicalGapInsight;
    private String seniorityInsight;
    private String contextDriftInsight;
    private String scalabilityInsight;

    private LocalDateTime createdAt;
}