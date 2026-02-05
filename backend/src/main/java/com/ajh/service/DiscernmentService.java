package com.ajh.service;

import com.ajh.dto.TalentResponseDTO;
import com.ajh.repository.TalentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DiscernmentService {

    private final TalentRepository repository;

    public DiscernmentService(TalentRepository repository) {
        this.repository = repository;
    }

    public List<TalentResponseDTO> getRankedTalents() {
        return repository.findAllByOrderByMatchScoreDesc()
            .stream()
            .map(t -> new TalentResponseDTO(
                t.getId(),
                t.getName(),
                t.getRole(),
                t.getMatchScore().intValue(),
                t.getAssessmentStatus()
            ))
            .collect(Collectors.toList());
    }
}