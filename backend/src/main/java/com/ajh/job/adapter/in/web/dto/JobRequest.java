package com.ajh.job.adapter.in.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JobRequest {
    private String title;
    private String description;
    private String company;
    private String salaryRange; // Adicionado para resolver erro de compilação
    private String location;    // Adicionado para resolver erro de compilação
}