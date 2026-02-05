package com.ajh.candidate.infrastructure.client;

import com.ajh.candidate.domain.MatchResultDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Component
@Slf4j
public class GroqClient {

    private final String apiUrl;
    private final String apiKey;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 🛡️ CONSTRUTOR DE GOVERNANÇA:
     * Sincronizado com ajh.groq.* no application.yml
     */
    public GroqClient(
            @Value("${ajh.groq.url:https://api.groq.com/openai/v1/chat/completions}") String apiUrl,
            @Value("${ajh.groq.key}") String apiKey, 
            RestTemplate restTemplate,
            ObjectMapper objectMapper) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        
        log.info("🏛️ [AI-GATEWAY] Neural Interface initialized on: {}", apiUrl);
    }

    /**
     * Gera uma resposta genérica de texto da Groq (Utilizado para extração de Job Description).
     */
    public String generateResponse(String prompt) {
        long startTime = System.currentTimeMillis();
        try {
            log.info("📡 [AI-GATEWAY] Sending generic inference request...");
            
            HttpHeaders headers = createHeaders();
            Map<String, Object> requestBody = createBaseRequestBody("user", prompt);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            String content = extractContent(response);
            log.info("✅ [AI-GATEWAY] Response received in {}ms", (System.currentTimeMillis() - startTime));
            return content;
        } catch (Exception e) {
            log.error("❌ [AI-GATEWAY] Inference failure: {}", e.getMessage());
            return "ERROR_IN_NEURAL_EXTRACTION";
        }
    }

    /**
     * Análise específica de Match entre Candidato e Vaga.
     */
    public MatchResultDto getAiAnalysis(String resumeText, String jobDescription) {
        try {
            log.info("📡 [AI-GATEWAY] Initiating Deep Match Analysis...");
            
            HttpHeaders headers = createHeaders();
            
            String systemPrompt = "ACT AS A TECH RECRUITER. Analyze resume against JD. " +
                                  "Return ONLY valid JSON: " +
                                  "{\"matchScore\": double, \"fitJustification\": \"string\", \"missingSkills\": []}";
            
            String userContent = String.format("Resume: %s \n\nJob: %s", resumeText, jobDescription);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.3-70b-versatile");
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userContent)
            ));
            requestBody.put("response_format", Map.of("type", "json_object"));
            requestBody.put("temperature", 0.0);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);

            String content = extractContent(response);
            return parseJson(content);

        } catch (Exception e) {
            log.error("❌ [AI-GATEWAY] Matching failure: {}", e.getMessage());
            return MatchResultDto.builder()
                    .matchScore(0.0)
                    .fitJustification("Service Handshake Error: " + e.getMessage())
                    .build();
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        return headers;
    }

    private Map<String, Object> createBaseRequestBody(String role, String content) {
        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama-3.3-70b-versatile");
        body.put("messages", List.of(Map.of("role", role, "content", content)));
        body.put("temperature", 0.0);
        return body;
    }

    private String extractContent(ResponseEntity<Map> response) {
        if (response.getBody() != null && response.getBody().containsKey("choices")) {
            List<?> choices = (List<?>) response.getBody().get("choices");
            if (!choices.isEmpty()) {
                Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
                Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
                return (String) message.get("content");
            }
        }
        return "";
    }

    private MatchResultDto parseJson(String json) {
        try {
            // Limpeza defensiva de markdown
            String cleanedJson = json.replace("```json", "").replace("```", "").trim();
            return objectMapper.readValue(cleanedJson, MatchResultDto.class);
        } catch (Exception e) {
            log.error("❌ [AI-GATEWAY] Semantic Corruption (JSON Parse Error): {}", e.getMessage());
            return MatchResultDto.builder()
                    .matchScore(0.0) 
                    .fitJustification("Neural Parsing Failure")
                    .build();
        }
    }
}