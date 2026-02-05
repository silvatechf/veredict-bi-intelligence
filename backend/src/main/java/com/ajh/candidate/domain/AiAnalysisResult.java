package com.ajh.candidate.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AiAnalysisResult {
    private String extractedText;
    private List<String> skills;
    private String seniority;
    private String summary;
    private List<Double> embedding;
}