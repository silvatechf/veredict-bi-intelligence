package com.ajh.candidate.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "usage_stats")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class UsageStatsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private LocalDate usageDate;
    private int aiCallsCount;
}