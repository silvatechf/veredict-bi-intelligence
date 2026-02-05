// backend/src/main/java/com/ajh/candidate/application/port/out/CandidatePersistencePort.java
package com.ajh.candidate;

import com.ajh.candidate.domain.Candidate;
import java.util.Optional;
import java.util.UUID;

// Interface de Porta de Saída para operações de banco de dados do Candidato
public interface CandidatePersistencePort {
    
    Candidate save(Candidate candidate);
    
    Optional<Candidate> findById(UUID id);
    
    // Métodos futuros: buscar candidatos por filtros, etc.
}