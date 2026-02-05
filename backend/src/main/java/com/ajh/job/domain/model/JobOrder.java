package com.ajh.job.domain.model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter 
@Setter 
@Builder(toBuilder = true) // Permite criar cópias alteradas se necessário
@NoArgsConstructor 
@AllArgsConstructor
public class JobOrder {
    private Long id;
    private String title;
    private String company;
    private String description;
    private List<String> requirements;
    private String seniority;
    private String salaryRange; 
    private String location;
    private String status;

    // ✅ ADICIONADO: O campo que o Controller e o Service estão gritando por ele
    private LocalDateTime createdAt; 
}