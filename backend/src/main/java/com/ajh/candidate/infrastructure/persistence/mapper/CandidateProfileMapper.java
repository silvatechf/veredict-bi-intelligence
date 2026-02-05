package com.ajh.candidate.infrastructure.persistence.mapper;

import com.ajh.candidate.domain.CandidateProfile;
import com.ajh.candidate.infrastructure.persistence.entity.CandidateProfileEntity;

public class CandidateProfileMapper {

    public static CandidateProfileEntity toEntity(CandidateProfile domain) {
        if (domain == null) return null;
        
        return CandidateProfileEntity.builder()
                .candidateId(domain.getCandidateId())
                .extractedText(domain.getExtractedText())
                .seniority(domain.getSeniority())
                .skills(domain.getSkills())
                // ✅ Campos de Inteligência (Garantindo persistência do porquê do score)
                .aiSummary(domain.getAiSummary())
                .whyNotHundred(domain.getWhyNotHundred())
                .build();
    }

    public static CandidateProfile toDomain(CandidateProfileEntity entity) {
        if (entity == null) return null;
        
        return CandidateProfile.builder()
                .candidateId(entity.getCandidateId())
                .extractedText(entity.getExtractedText())
                .seniority(entity.getSeniority())
                .skills(entity.getSkills())
                // ✅ Campos de Inteligência (Trazendo do banco para a UI)
                .aiSummary(entity.getAiSummary())
                .whyNotHundred(entity.getWhyNotHundred())
                .build();
    }
}