package com.ajh.matching.application.port.out;

import com.ajh.matching.domain.MatchResult;
import java.util.List;

/**
 * 🛡️ CONTRATO DE PERSISTÊNCIA SOBERANO
 * Define como o motor de matching se comunica com o banco de dados.
 */
public interface MatchRepositoryPort {
    
    // Salva o resultado da colisão neural
    MatchResult save(MatchResult matchResult);
    
    // Busca o histórico de um talento específico
    List<MatchResult> findByCandidateId(Long candidateId);
    
    /**
     * 🎯 ESSENCIAL PARA O DASHBOARD:
     * Recupera todos os talentos analisados para uma vaga específica.
     */
    List<MatchResult> findByJobId(Long jobId);
}