package com.ajh.matching.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "matches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatchResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long candidateId;

    @Column(nullable = false)
    private Long jobId;

    private Double matchScore;

    @Column(columnDefinition = "TEXT")
    private String fitJustification;

    // --- 📊 MATRIZ DE RISCO (VITAL PARA O RADAR NEON) ---
    private Double technicalGapRisk;
    private Double seniorityMismatchRisk;
    private Double contextDriftRisk;
    private Double scalabilityRisk;

    // --- 💡 INSIGHTS ESTRATÉGICOS (VITAL PARA O EXECUTIVO) ---
    @Column(columnDefinition = "TEXT")
    private String technicalGapInsight;
    
    @Column(columnDefinition = "TEXT")
    private String seniorityInsight;
    
    @Column(columnDefinition = "TEXT")
    private String contextDriftInsight;
    
    @Column(columnDefinition = "TEXT")
    private String scalabilityInsight;

    @ElementCollection
    @CollectionTable(name = "match_missing_skills", joinColumns = @JoinColumn(name = "match_id"))
    @Column(name = "skill_name")
    private List<String> missingSkills;

    private LocalDateTime createdAt;
}