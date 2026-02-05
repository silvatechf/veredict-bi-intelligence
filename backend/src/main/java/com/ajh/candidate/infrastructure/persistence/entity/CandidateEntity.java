package com.ajh.candidate.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import com.ajh.candidate.domain.Role;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "candidates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "natural_identity", nullable = false, unique = true)
    private String naturalIdentity;

    @Column(name = "node_id")
    private String nodeId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    private String password;

    // 🛡️ SECURITY ROLE: Fixo para controle de acesso (ADMIN, CANDIDATE, RECRUITER)
    // É este campo que o ranking usará no filtro: WHERE role = 'CANDIDATE'
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.CANDIDATE;

    // 🎯 PROFESSIONAL TARGET: O cargo extraído pela IA (Ex: Full-Stack Developer)
    // Isso evita que a IA sobrescreva o 'Role' de segurança do sistema.
    @Column(name = "professional_target")
    private String professionalTarget;

    // --- 📊 NEURAL SCORES (0-100) ---
    @Column(name = "match_score")
    private Double matchScore;

    @Column(name = "technical_fit") 
    private Double technicalFit;

    @Column(name = "context_fit") 
    private Double contextFit;

    @Column(name = "risk_factor") 
    private Double riskFactor;

    // --- 🚀 MATRIX DE RISCO (0-100) ---
    @Column(name = "technical_gap_risk")
    private Double technicalGapRisk;

    @Column(name = "seniority_mismatch_risk")
    private Double seniorityMismatchRisk;

    @Column(name = "context_drift_risk")
    private Double contextDriftRisk;

    @Column(name = "scalability_risk")
    private Double scalabilityRisk;

    // --- 💡 INSIGHTS BRUTOS ---
    @Column(name = "technical_gap_insight", columnDefinition = "TEXT")
    private String technicalGapInsight;

    @Column(name = "seniority_insight", columnDefinition = "TEXT")
    private String seniorityInsight;

    @Column(name = "context_drift_insight", columnDefinition = "TEXT")
    private String contextDriftInsight;

    @Column(name = "scalability_insight", columnDefinition = "TEXT")
    private String scalabilityInsight;

    @Column(name = "assessment_status")
    private String assessmentStatus;

    @Column(name = "seniority")
    private String seniority;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "candidate_tech_skills", joinColumns = @JoinColumn(name = "candidate_id"))
    @Column(name = "skill_name")
    @Builder.Default 
    private List<String> skills = new ArrayList<>();

    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    @Column(name = "interview_questions", columnDefinition = "TEXT")
    private String interviewQuestions;

    @Column(name = "why_not_hundred", columnDefinition = "TEXT")
    private String whyNotHundred;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        // Garante que o Role nunca seja nulo no momento do Insert
        if (this.role == null) this.role = Role.CANDIDATE;
        
        if (this.skills == null) this.skills = new ArrayList<>();
        
        this.technicalGapRisk = Optional.ofNullable(this.technicalGapRisk).orElse(0.0);
        this.seniorityMismatchRisk = Optional.ofNullable(this.seniorityMismatchRisk).orElse(0.0);
        this.contextDriftRisk = Optional.ofNullable(this.contextDriftRisk).orElse(0.0);
        this.scalabilityRisk = Optional.ofNullable(this.scalabilityRisk).orElse(0.0);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}