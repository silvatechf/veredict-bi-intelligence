package com.ajh.job.domain;

import lombok.*;
import java.util.List;
import java.time.LocalDateTime;

@Getter 
@Setter 
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class Job {
    
    private Long id;
    private String title;
    private String company; 
    private String description;
    private List<String> requirements; // Hard skills extraídas/inferidas
    private String seniority;           // JUNIOR, MID, SENIOR, ARCHITECT
    private String status;              // OPEN, CLOSED
    private LocalDateTime createdAt;
}