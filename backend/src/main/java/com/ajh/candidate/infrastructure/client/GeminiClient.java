package com.ajh.candidate.infrastructure.client;

import com.ajh.candidate.domain.MatchResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@RequiredArgsConstructor
// Nível Google: Só cria este Bean se a URL do Gemini estiver no YAML. 
// Se não estiver, o sistema ignora o Gemini e sobe normalmente.
@ConditionalOnProperty(name = "gemini.api.url") 
public class GeminiClient {

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.key:}") // O ":" garante que o Spring não morra se a chave faltar
    private String apiKey;

    private final RestTemplate restTemplate;

    public MatchResultDto getAiAnalysis(String resumeText, String jobDescription) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("⚠️ Gemini API Key is missing. Skipping Gemini analysis.");
            return null;
        }
        
        log.info("🚀 Initiating analysis with Gemini...");
        // TODO: Implementar chamada real para a API do Google Gemini
        return null; 
    }
}