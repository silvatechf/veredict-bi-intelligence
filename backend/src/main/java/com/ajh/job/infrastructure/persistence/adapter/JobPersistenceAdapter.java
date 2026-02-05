package com.ajh.job.infrastructure.persistence.adapter;

import com.ajh.job.application.port.out.JobRepositoryPort;
import com.ajh.job.domain.model.JobOrder;
import com.ajh.job.infrastructure.persistence.entity.JobOrderEntity;
import com.ajh.job.infrastructure.persistence.mapper.JobOrderMapper;
import com.ajh.job.infrastructure.persistence.repository.JpaJobRepository;
import com.ajh.job.infrastructure.persistence.repository.JpaJobMatchRepository; // 🚨 Adicionado
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JobPersistenceAdapter implements JobRepositoryPort {

    private final JpaJobRepository repository;
    private final JpaJobMatchRepository matchRepository; // 🚨 Injetado para gerenciar a Arena
    private final JobOrderMapper mapper;

    @Override
    public JobOrder save(JobOrder jobOrder) {
        JobOrderEntity entity = mapper.toEntity(jobOrder);
        JobOrderEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<JobOrder> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<JobOrder> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    /**
     * ⚡ SINCRONIA DE ARENA (MÉTODO CHAVE):
     * Este método deve ser exposto na JobRepositoryPort e chamado pelo MatchMakerService.
     * Ele garante que o DELETE do ranking antigo seja processado ANTES do novo INSERT.
     */
    @Override
    public void flushMatchArena() {
        matchRepository.flush(); 
    }

    @Override
    public void deleteMatchesByJobId(Long jobId) {
        matchRepository.deleteByJobId(jobId);
    }
}