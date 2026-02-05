package com.ajh.candidate.infrastructure.persistence.repository;

import com.ajh.candidate.infrastructure.persistence.entity.UsageStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface JpaUsageStatsRepository extends JpaRepository<UsageStatsEntity, Long> {

    /**
     * Google Standard - SaaS Metering:
     * Busca o registro de consumo de IA para um usuário específico em uma data.
     * * @param userId ID do candidato/usuário solicitante
     * @param usageDate Data da consulta (geralmente LocalDate.now())
     * @return Optional contendo as estatísticas de uso se existirem
     */
    Optional<UsageStatsEntity> findByUserIdAndUsageDate(Long userId, LocalDate usageDate);
}