package com.ajh.candidate.application.port.out;

import java.util.Optional;

import com.ajh.candidate.domain.CandidateProfile;

public interface CandidateProfileRepositoryPort {
    Optional<CandidateProfile> findByCandidateId(Long candidateId);
    void save(CandidateProfile profile);
}