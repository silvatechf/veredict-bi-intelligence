package com.ajh.job.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_orders")
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class JobOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "company_name", nullable = false) 
    private String company;

    // 🛡️ ESSENCIAL: Senioridade inferida pela IA v18.5
    private String seniority;

    private String location;
    
    @Column(name = "salary_range")
    private String salaryRange;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    /**
     * ✅ EAGER LOADING: 
     * Carrega os requisitos instantaneamente para evitar LazyInitializationException
     * durante o ciclo de ranking neural no MatchMakerService.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "job_requirements", 
        joinColumns = @JoinColumn(name = "job_id")
    )
    @Column(name = "requirement_name")
    @Builder.Default
    private List<String> requirements = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = JobStatus.OPEN;
        }
    }
}