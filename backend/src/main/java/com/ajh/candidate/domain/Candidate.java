package com.ajh.candidate.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Getter 
@Setter 
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity 
@Table(name = "candidates")
public class Candidate implements UserDetails {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "natural_identity", unique = true, nullable = false)
    private String naturalIdentity;

    @Column(name = "node_id")
    private String nodeId;

    @Column(unique = true, nullable = false)
    private String email;
    
    private String name;
    private String password;

    // 🛡️ SECURITY ROLE: Determina quem o usuário É (ADMIN, CANDIDATE)
    @Enumerated(EnumType.STRING)
    private Role role;

    // 🎯 PROFESSIONAL TARGET: Determina o que o usuário FAZ (Job Title extraído pela IA)
    // Este campo resolve o conflito que deixava o ranking vazio!
    @Column(name = "professional_target")
    private String professionalTarget;

    // --- 📊 NEURAL SCORES (Escala 0-100) ---
    @Column(name = "match_score")
    private Double matchScore;

    @Column(name = "technical_fit")
    private Double technicalFit;

    @Column(name = "context_fit")
    private Double contextFit;

    @Column(name = "risk_factor")
    private Double riskFactor;

    // --- 🚀 MATRIX DE RISCO (Escala 0-100) ---
    @Column(name = "technical_gap_risk")
    private Double technicalGapRisk;

    @Column(name = "seniority_mismatch_risk")
    private Double seniorityMismatchRisk;

    @Column(name = "context_drift_risk")
    private Double contextDriftRisk;

    @Column(name = "scalability_risk")
    private Double scalabilityRisk;

    // --- 💡 INSIGHTS ESTRATÉGICOS ---
    @Column(name = "technical_gap_insight", columnDefinition = "TEXT")
    private String technicalGapInsight;

    @Column(name = "seniority_insight", columnDefinition = "TEXT")
    private String seniorityInsight;

    @Column(name = "context_drift_insight", columnDefinition = "TEXT")
    private String contextDriftInsight;

    @Column(name = "scalability_insight", columnDefinition = "TEXT")
    private String scalabilityInsight;

    private String seniority;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "candidate_tech_skills", joinColumns = @JoinColumn(name = "candidate_id"))
    @Column(name = "skill_name")
    private List<String> skills;

    @Column(name = "assessment_status")
    private String assessmentStatus;

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

    // --- 🛡️ CICLO DE VIDA ---

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.role == null) this.role = Role.CANDIDATE;
        if (this.skills == null) this.skills = new ArrayList<>();
        
        // Inicialização segura para o Radar
        this.technicalGapRisk = Optional.ofNullable(this.technicalGapRisk).orElse(0.0);
        this.seniorityMismatchRisk = Optional.ofNullable(this.seniorityMismatchRisk).orElse(0.0);
        this.contextDriftRisk = Optional.ofNullable(this.contextDriftRisk).orElse(0.0);
        this.scalabilityRisk = Optional.ofNullable(this.scalabilityRisk).orElse(0.0);
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // --- 🛠️ MÉTODOS AUXILIARES ---

    public void setInterviewQuestionsFromList(List<String> questions) {
        if (questions == null || questions.isEmpty()) {
            this.interviewQuestions = "";
            return;
        }
        this.interviewQuestions = questions.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(q -> q.length() > 5)
                .distinct() 
                .collect(Collectors.joining("\n"));
    }

    // --- 🔐 SPRING SECURITY (UserDetails) ---

    @Override 
    public Collection<? extends GrantedAuthority> getAuthorities() { 
        return List.of(new SimpleGrantedAuthority("ROLE_" + (role != null ? role.name() : "CANDIDATE"))); 
    }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}