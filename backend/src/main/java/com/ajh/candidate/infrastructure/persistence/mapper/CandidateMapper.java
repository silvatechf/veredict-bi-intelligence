package com.ajh.candidate.infrastructure.persistence.mapper;

import com.ajh.candidate.domain.Candidate;
import com.ajh.candidate.domain.Role;
import com.ajh.candidate.infrastructure.persistence.entity.CandidateEntity;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CandidateMapper {

    /**
     * 📥 Entidade (Postgres) -> Domínio (Java Core)
     */
    public Candidate toDomain(CandidateEntity entity) {
        if (entity == null) return null;

        return Candidate.builder()
                .id(entity.getId())
                .naturalIdentity(entity.getNaturalIdentity())
                .nodeId(entity.getNodeId())
                .email(entity.getEmail())
                .name(entity.getName())
                .password(entity.getPassword())
                // 🛡️ Mantém a segurança: role é quem o usuário É (CANDIDATE)
                .role(entity.getRole())
                // 🎯 Novo Campo: O que o usuário FAZ (Job Title da IA)
                .professionalTarget(entity.getProfessionalTarget())
                .matchScore(entity.getMatchScore())
                .technicalFit(entity.getTechnicalFit())
                .contextFit(entity.getContextFit())
                .riskFactor(entity.getRiskFactor())
                .technicalGapRisk(entity.getTechnicalGapRisk())
                .seniorityMismatchRisk(entity.getSeniorityMismatchRisk())
                .contextDriftRisk(entity.getContextDriftRisk())
                .scalabilityRisk(entity.getScalabilityRisk())
                .technicalGapInsight(entity.getTechnicalGapInsight())
                .seniorityInsight(entity.getSeniorityInsight())
                .contextDriftInsight(entity.getContextDriftInsight())
                .scalabilityInsight(entity.getScalabilityInsight())
                .seniority(entity.getSeniority())
                .skills(entity.getSkills() != null ? new ArrayList<>(entity.getSkills()) : new ArrayList<>())
                .aiSummary(entity.getAiSummary())
                .interviewQuestions(entity.getInterviewQuestions())
                .whyNotHundred(entity.getWhyNotHundred())
                .assessmentStatus(entity.getAssessmentStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * 📤 Domínio (Java Core) -> Entidade (Postgres)
     */
    public CandidateEntity toEntity(Candidate domain) {
        if (domain == null) return null;

        List<String> cleanSkills = domain.getSkills() == null ? new ArrayList<>() :
                domain.getSkills().stream()
                        .filter(s -> !s.equalsIgnoreCase("Standard Technical Stack"))
                        .filter(s -> !s.equalsIgnoreCase("DATA_EXTRACTION_MISSING"))
                        .collect(Collectors.toList());

        return CandidateEntity.builder()
                .id(domain.getId())
                .naturalIdentity(domain.getNaturalIdentity())
                .nodeId(domain.getNodeId())
                .email(domain.getEmail())
                .name(domain.getName())
                .password(domain.getPassword())
                // 🛡️ GARANTIA TÁTICA: O role no banco será SEMPRE CANDIDATE para o ranking achar
                .role(domain.getRole() != null ? domain.getRole() : Role.CANDIDATE)
                // 🎯 O cargo da IA vai para o campo específico de BI
                .professionalTarget(domain.getProfessionalTarget())
                .matchScore(domain.getMatchScore())
                .technicalFit(domain.getTechnicalFit())
                .contextFit(domain.getContextFit())
                .riskFactor(domain.getRiskFactor())
                .technicalGapRisk(domain.getTechnicalGapRisk())
                .seniorityMismatchRisk(domain.getSeniorityMismatchRisk())
                .contextDriftRisk(domain.getContextDriftRisk())
                .scalabilityRisk(domain.getScalabilityRisk())
                .technicalGapInsight(domain.getTechnicalGapInsight())
                .seniorityInsight(domain.getSeniorityInsight())
                .contextDriftInsight(domain.getContextDriftInsight())
                .scalabilityInsight(domain.getScalabilityInsight())
                .seniority(domain.getSeniority())
                .skills(cleanSkills)
                .aiSummary(domain.getAiSummary())
                .interviewQuestions(domain.getInterviewQuestions())
                .whyNotHundred(domain.getWhyNotHundred())
                .assessmentStatus(domain.getAssessmentStatus())
                .build();
    }
}