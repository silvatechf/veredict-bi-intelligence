package com.ajh.candidate.application.port.out;

import com.ajh.candidate.domain.Candidate;
import com.ajh.candidate.domain.CandidateProfile;
import com.ajh.candidate.domain.Role;
import java.util.Optional;
import java.util.List;

/**
 * Port de saída soberano para persistência de Candidatos e Perfis Neurais.
 */
public interface CandidateRepositoryPort {
    
    // ... (métodos anteriores permanecem iguais)

    Optional<Candidate> findByNaturalIdentity(String naturalIdentity);
    Optional<Candidate> findByEmail(String email);
    Optional<Candidate> findById(Long id);
    Candidate save(Candidate candidate);
    Candidate saveAndFlush(Candidate candidate); 
    List<Candidate> findAll();
    List<Candidate> findByRoleAndAssessmentStatus(Role role, String assessmentStatus);
    long countByRoleAndAssessmentStatus(Role role, String assessmentStatus);
    void deleteById(Long id); 
    boolean existsById(Long id); 

    Optional<CandidateProfile> findProfileByCandidateId(Long candidateId);
    void saveProfile(CandidateProfile profile);
    void saveAll(List<Candidate> candidates);

    void flush(); 
}