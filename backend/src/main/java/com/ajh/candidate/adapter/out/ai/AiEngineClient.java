package com.ajh.candidate.adapter.out.ai;

import com.ajh.candidate.domain.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j 
@Component
public class AiEngineClient {
    private final RestTemplate restTemplate;
    private final String aiEngineUrl;
    private final ObjectMapper objectMapper;

    public AiEngineClient(RestTemplate restTemplate, 
                          @Value("${ajh.ai-engine.url}") String aiEngineUrl, 
                          ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.aiEngineUrl = aiEngineUrl.replace("llocalhost", "localhost").replaceAll("/$", "");
        
        this.objectMapper = objectMapper.copy()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public CandidateProfileDto analyze(MultipartFile file) {
        log.info("📡 [AI-CLIENT] Initiating transfer to Neural Engine: {}", file.getOriginalFilename());
        try {
            byte[] fileBytes = file.getBytes();
            if (fileBytes.length == 0) throw new RuntimeException("Arquivo vazio detectado.");

            ByteArrayResource fileResource = new ByteArrayResource(fileBytes) {
                @Override
                public String getFilename() { return file.getOriginalFilename(); }
            };

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", fileResource);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            String url = aiEngineUrl + "/analyze-resume/analyze";
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(body, headers), String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), CandidateProfileDto.class);
            }
            throw new RuntimeException("Neural Engine Status: " + response.getStatusCode());

        } catch (Exception e) {
            log.error("❌ [AI FATAL]: Ingestion failure: {}", e.getMessage());
            return CandidateProfileDto.builder()
                    .name("Audit Failure")
                    .assessmentStatus("INVALID_AI_RESPONSE")
                    .aiSummary("Neural Bridge Error: " + e.getMessage())
                    .build();
        }
    }

    /**
     * 🎯 RECALCULATE: Sincronizado para tratar Erros de Cota (429)
     */
    public CandidateProfileDto recalculate(String summary, String jd) {
        log.info("🧠 [NEURAL-ENGINE] Recalibrating candidate match against new JD...");
        try {
            Map<String, String> request = Map.of(
                "summary", (summary != null && !summary.isBlank()) ? summary : "Audit context pending.",
                "jd", (jd != null && !jd.isBlank()) ? jd : "Job context pending."
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            String url = aiEngineUrl + "/analyze-resume/recalculate";

            ResponseEntity<String> response = restTemplate.postForEntity(url, new HttpEntity<>(request, headers), String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                log.info("🎯 [AI-CLIENT] Recalculation successful.");
                return objectMapper.readValue(response.getBody(), CandidateProfileDto.class);
            }
            
            throw new RuntimeException("Unexpected Status: " + response.getStatusCode());

        } catch (HttpClientErrorException.TooManyRequests e) {
            log.error("⚠️ [RATE LIMIT] Groq API is throttled. Returning safe fallback.");
            return CandidateProfileDto.builder()
                    .matchScore(0.1)
                    .technicalFit(0.1)
                    .contextFit(0.1)
                    .whyNotHundred("API Rate Limit Reached. Please wait a few minutes.")
                    .aiSummary("Rate Limit Error (429)")
                    .build();
        } catch (Exception e) {
            log.error("❌ [RECALCULATE ERROR]: {}", e.getMessage());
            return CandidateProfileDto.builder()
                    .matchScore(10.0)
                    .technicalFit(10.0)
                    .contextFit(10.0)
                    .aiSummary("Matrix collision failure.")
                    .whyNotHundred("Neural link error.")
                    .build();
        }
    }

    public CandidateProfileDto reanalyze(String profileSummary, String jobDescription) {
        return recalculate(profileSummary, jobDescription);
    }
}