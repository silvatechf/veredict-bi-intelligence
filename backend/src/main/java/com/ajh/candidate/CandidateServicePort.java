// backend/src/main/java/com/ajh/candidate/application/port/in/CandidateServicePort.java
package com.ajh.candidate;

import org.springframework.web.multipart.MultipartFile;

import com.ajh.candidate.domain.CandidateProfileDto;

import java.util.UUID;

public interface CandidateServicePort {
    
    CandidateProfileDto processCvUpload(UUID candidateId, MultipartFile cvFile);
}