package com.ajh.candidate.application.port.out;

import com.ajh.candidate.domain.CandidateProfileDto;
import org.springframework.web.multipart.MultipartFile;

public interface AiEnginePort {
    CandidateProfileDto analyze(MultipartFile file);
}