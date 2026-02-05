package com.ajh.candidate.application.port.in;

import com.ajh.candidate.domain.CandidateProfileDto;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface CandidateUseCase {
    List<CandidateProfileDto> getRankedCandidates();
    CandidateProfileDto uploadAndAnalyze(MultipartFile file, String email);
    CandidateProfileDto performDeepAnalysis(Long id);
    void deleteCandidate(Long id);
    
    /**
     * 🎯 NEURAL CALIBRATION PORT: 
     * Recalcula o match de todos os nós com base em uma Job Description específica.
     */
    void recalculateAllMatches(String jobDescription);

    // Mantido para satisfazer a busca por contexto de segurança/identidade
    CandidateProfileDto getProfileByEmail(String email);
}