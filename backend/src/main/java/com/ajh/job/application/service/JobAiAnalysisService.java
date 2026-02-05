package com.ajh.job.application.service;

import com.ajh.candidate.infrastructure.client.GroqClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobAiAnalysisService {

    private final GroqClient groqClient;

    /**
     * 🛡️ EXTRAÇÃO E INFERÊNCIA: 
     * Se a descrição for vaga, a IA agora é instruída a inferir a stack 
     * padrão para o título do cargo, garantindo que o ranking nunca rode no vazio.
     */
    public List<String> extractRequirements(String description) {
        log.info("📡 [JOB-INTELLIGENCE] Starting deep extraction and inference...");
        
        String prompt = """
            As a Technical Recruiter, analyze this job description: "%s"
            
            MANDATES:
            1. Extract explicit technical hard skills mentioned.
            2. If technical requirements are missing or vague, INFER the standard tech stack 
               expected for this role based on the title and seniority.
            3. Return ONLY a comma-separated list of tools/languages.
            4. NO PROSE. NO EXPLANATIONS. 
            
            Format: Java, Spring Boot, AWS, Docker
            """.formatted(description);

        try {
            String response = groqClient.generateResponse(prompt);
            
            // 🛡️ Filtro de Segurança contra contaminação de texto explicativo
            List<String> requirements = Arrays.stream(response.split(","))
                    .map(String::trim)
                    .filter(s -> s.length() > 1 && s.length() < 30)
                    .filter(s -> !s.toUpperCase().contains("MENTIONED"))
                    .filter(s -> !s.toUpperCase().contains("PROVIDED"))
                    .collect(Collectors.toList());

            if (requirements.isEmpty()) {
                log.warn("⚠️ [JOB-INTELLIGENCE] AI returned empty requirements. Using safety fallback.");
                return List.of("Software Engineering", "Technical Documentation");
            }

            log.info("✅ [JOB-INTELLIGENCE] Requirements established: {}", requirements);
            return requirements;
        } catch (Exception e) {
            log.error("❌ AI Analysis failed", e);
            return List.of("General Software Development");
        }
    }

    public String estimateSeniority(String description) {
        String prompt = """
            Analyze the seniority for this job: "%s"
            Return only ONE word: JUNIOR, MID, SENIOR, or ARCHITECT.
            """.formatted(description);
        try {
            String result = groqClient.generateResponse(prompt).toUpperCase().trim();
            // Limpeza de segurança para garantir apenas a palavra chave
            if (result.contains("SENIOR")) return "SENIOR";
            if (result.contains("ARCHITECT")) return "ARCHITECT";
            if (result.contains("JUNIOR")) return "JUNIOR";
            return "MID";
        } catch (Exception e) {
            return "MID";
        }
    }
}