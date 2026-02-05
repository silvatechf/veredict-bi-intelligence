package com.ajh.candidate.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MatchVeredictDto {

    private Long candidateId;
    private Long jobId;

    // --- 🎯 SCORES ESSENCIAIS (Para matar o bug do 0%) ---

    @JsonProperty("finalScore")
    @JsonAlias({"match_score", "score", "final_score"})
    private Double finalScore;

    @JsonProperty("technicalFit")
    @JsonAlias({"technical_fit", "tech_fit", "technical_score"})
    private Double technicalFit;

    @JsonProperty("contextFit")
    @JsonAlias({"context_fit", "context_score"})
    private Double contextFit;

    // --- 🛡️ CONFIANÇA E RISCO ---

    @JsonProperty("confidenceLevel")
    @JsonAlias({"confidence_level", "risk_factor", "risk"})
    private String confidenceLevel; // LOW, MEDIUM, HIGH

    @JsonProperty("summary")
    @JsonAlias({"ai_summary", "fit_justification", "analysis_summary"})
    private String summary;

    @JsonProperty("topMissingSkills")
    @JsonAlias({"missing_skills", "gaps", "top_missing_skills"})
    private List<String> topMissingSkills;

    private String analysisTimestamp;

    // --- GETTERS RESILIENTES (Proteção contra NullPointerException) ---

    public Double getFinalScore() { return finalScore != null ? finalScore : 0.0; }
    public Double getTechnicalFit() { return technicalFit != null ? technicalFit : 0.0; }
    public Double getContextFit() { return contextFit != null ? contextFit : 0.0; }

    public List<String> getTopMissingSkills() {
        return topMissingSkills != null ? topMissingSkills : new ArrayList<>();
    }
}