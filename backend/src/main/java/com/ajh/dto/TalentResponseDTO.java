package com.ajh.dto;

public record TalentResponseDTO(
    Long id,
    String name,
    String role,
    Integer match, // Convertido para Integer para o Gauge do Front
    String status
) {}