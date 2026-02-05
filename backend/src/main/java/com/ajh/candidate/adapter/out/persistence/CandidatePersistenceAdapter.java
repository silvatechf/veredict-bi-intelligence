package com.ajh.candidate.adapter.out.persistence;

import com.ajh.candidate.application.port.out.CandidateRepositoryPort;
import com.ajh.candidate.domain.Candidate;
import com.ajh.candidate.domain.CandidateProfile;
import com.ajh.candidate.domain.Role;
import com.ajh.candidate.infrastructure.persistence.entity.CandidateEntity;
import com.ajh.candidate.infrastructure.persistence.mapper.CandidateMapper;
import com.ajh.candidate.infrastructure.persistence.repository.JpaCandidateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CandidatePersistenceAdapter implements CandidateRepositoryPort {

    private final JpaCandidateRepository jpaRepository;
    private final CandidateMapper candidateMapper;

    @Override
    public Optional<Candidate> findByNaturalIdentity(String naturalIdentity) {
        return jpaRepository.findByNaturalIdentity(naturalIdentity)
                .map(candidateMapper::toDomain);
    }

    @Override
    public Optional<Candidate> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(candidateMapper::toDomain);
    }

    @Override
    public Optional<Candidate> findById(Long id) {
        return jpaRepository.findById(id).map(candidateMapper::toDomain);
    }

    @Override
    public Candidate save(Candidate candidate) {
        CandidateEntity entity = candidateMapper.toEntity(candidate);
        CandidateEntity saved = jpaRepository.save(entity);
        return candidateMapper.toDomain(saved);
    }

    @Override
    public Candidate saveAndFlush(Candidate candidate) {
        CandidateEntity entity = candidateMapper.toEntity(candidate);
        CandidateEntity saved = jpaRepository.saveAndFlush(entity);
        return candidateMapper.toDomain(saved);
    }

    @Override
    public void saveAll(List<Candidate> candidates) {
        if (candidates == null || candidates.isEmpty()) return;

        List<CandidateEntity> entities = candidates.stream()
                .map(candidateMapper::toEntity)
                .collect(Collectors.toList());

        jpaRepository.saveAll(entities);
        log.info(">>> [PERSISTENCE] Batch saved {} neural nodes successfully.", entities.size());
    }

    /**
     * ⚡ SINCRONIA DE ARENA: Executa o Flush no repositório JPA.
     * Isso garante que os scores calculados pelo CandidateService 
     * fiquem visíveis para o JobService imediatamente.
     */
    @Override
    public void flush() {
        log.debug(">>> [PERSISTENCE] Executing Hard Flush for transaction synchronization.");
        jpaRepository.flush();
    }

    @Override
    public List<Candidate> findAll() {
        return jpaRepository.findAll().stream()
                .map(candidateMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Candidate> findByRoleAndAssessmentStatus(Role role, String assessmentStatus) {
        return jpaRepository.findByRoleAndAssessmentStatus(role, assessmentStatus)
                .stream()
                .map(candidateMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public long countByRoleAndAssessmentStatus(Role role, String assessmentStatus) {
        return jpaRepository.countByRoleAndAssessmentStatus(role, assessmentStatus);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public Optional<CandidateProfile> findProfileByCandidateId(Long candidateId) {
        return Optional.empty();
    }

    @Override
    public void saveProfile(CandidateProfile profile) {
        log.debug(">>> [PERSISTENCE] saveProfile called - implementation pending.");
    }
}