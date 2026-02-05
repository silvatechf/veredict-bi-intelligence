package com.ajh.job.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter 
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "job_matches")
public class JobMatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    // 📊 SCORES (Onde mora o seu 15.0%)
    @Column(name = "match_score")
    private Double matchScore;

    @Column(name = "technical_fit")
    private Double technicalFit;

    @Column(name = "context_fit")
    private Double contextFit;

    // 🔬 RISCOS (RADAR CHART) - Essencial para o polígono abrir
    @Column(name = "technical_gap_risk")
    private Double technicalGapRisk;

    @Column(name = "seniority_mismatch_risk")
    private Double seniorityMismatchRisk;

    @Column(name = "context_drift_risk")
    private Double contextDriftRisk;

    @Column(name = "scalability_risk")
    private Double scalabilityRisk;

    // 📝 INSIGHTS (Executive Briefing)
    @Column(name = "technical_gap_insight", columnDefinition = "TEXT")
    private String technicalGapInsight;

    @Column(name = "seniority_insight", columnDefinition = "TEXT")
    private String seniorityInsight;

    @Column(name = "context_drift_insight", columnDefinition = "TEXT")
    private String contextDriftInsight;

    @Column(name = "scalability_insight", columnDefinition = "TEXT")
    private String scalabilityInsight;

    @Column(name = "why_not_hundred", columnDefinition = "TEXT")
    private String whyNotHundred;

    @Column(columnDefinition = "TEXT")
    private String justification;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        // Fallbacks de segurança para o Recharts não bugar
        if (this.matchScore == null) this.matchScore = 15.0;
        if (this.technicalGapRisk == null) this.technicalGapRisk = 1.0;
        if (this.seniorityMismatchRisk == null) this.seniorityMismatchRisk = 1.0;
        if (this.contextDriftRisk == null) this.contextDriftRisk = 1.0;
        if (this.scalabilityRisk == null) this.scalabilityRisk = 1.0;
    }
}