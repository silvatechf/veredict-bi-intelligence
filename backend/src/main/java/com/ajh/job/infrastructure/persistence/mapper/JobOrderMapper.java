package com.ajh.job.infrastructure.persistence.mapper;

import com.ajh.job.domain.model.JobOrder;
import com.ajh.job.infrastructure.persistence.entity.JobOrderEntity;
import com.ajh.job.infrastructure.persistence.entity.JobStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class JobOrderMapper {

    public JobOrder toDomain(JobOrderEntity entity) {
        if (entity == null) return null;
        return JobOrder.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .company(entity.getCompany())
                .description(entity.getDescription())
                // Garante que a lista não seja nula para evitar problemas no MatchMaker
                .requirements(entity.getRequirements() != null ? new ArrayList<>(entity.getRequirements()) : new ArrayList<>())
                .seniority(entity.getSeniority())
                .salaryRange(entity.getSalaryRange())
                .location(entity.getLocation())
                .status(entity.getStatus() != null ? entity.getStatus().name() : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public JobOrderEntity toEntity(JobOrder domain) {
        if (domain == null) return null;
        return JobOrderEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .company(domain.getCompany())
                .description(domain.getDescription())
                .requirements(domain.getRequirements() != null ? new ArrayList<>(domain.getRequirements()) : new ArrayList<>())
                .seniority(domain.getSeniority())
                .salaryRange(domain.getSalaryRange())
                .location(domain.getLocation())
                // Tenta converter a String do domínio para o Enum da Entidade
                .status(domain.getStatus() != null ? JobStatus.valueOf(domain.getStatus().toUpperCase()) : JobStatus.OPEN)
                .build();
    }
}