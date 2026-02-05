package com.ajh.repository;

import com.ajh.model.Talent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TalentRepository extends JpaRepository<Talent, Long> {
    // Busca todos os talentos e já traz ordenado pelo maior Match Score (Padrão Sovereign)
    List<Talent> findAllByOrderByMatchScoreDesc();
}