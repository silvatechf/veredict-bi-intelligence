package com.ajh.candidate.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "candidate_profiles")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class CandidateProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "candidate_id") 
    private Long candidateId;
    
    @Column(columnDefinition = "TEXT")
    private String extractedText;
    
    private String seniority;

    // ✅ NOVOS CAMPOS: Para persistir a inteligência da análise
    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    @Column(name = "why_not_hundred", columnDefinition = "TEXT")
    private String whyNotHundred;

    @ElementCollection
    @CollectionTable(name = "candidate_skills", joinColumns = @JoinColumn(name = "profile_id"))
    @Column(name = "skill_name")
    private List<String> skills;
}