package com.ajh.candidate.infrastructure.config;

import com.ajh.candidate.domain.Candidate;
import com.ajh.candidate.domain.Role;
import com.ajh.candidate.application.port.out.CandidateRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final CandidateRepositoryPort candidateRepository; 
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("🚀 [SYSTEM READY] Validando integridade do nó soberano...");

        String adminEmail = "fernandusilva84@gmail.com";

        // Verifica por email se o admin já existe
        candidateRepository.findByEmail(adminEmail).ifPresentOrElse(
            existing -> log.info("✅ [ONLINE] Fernando Silva já autenticado no sistema."),
            () -> {
                log.info("🌱 [SEED] Gerando credenciais de acesso soberanas: {}", adminEmail);
                
                Candidate admin = Candidate.builder()
                        .name("Fernando Silva")
                        .email(adminEmail)
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        // ✅ SOLUÇÃO DO BUG: Identidade natural fixa para evitar NullConstraint
                        .naturalIdentity("SYSTEM_ADMIN_ROOT_NODE") 
                        .seniority("Senior Fullstack & AI Architect")
                        .assessmentStatus("READY")
                        .skills(List.of("Java 21", "Spring Boot 3", "PostgreSQL", "React", "AI Integration"))
                        .matchScore(0.0)
                        .technicalFit(0.0)
                        .contextFit(0.0)
                        .riskFactor(0.0)
                        .build();

                // Salva via Port (que agora usa o Mapper v15 e o Persistence Adapter v15)
                candidateRepository.save(admin);
                log.info("🎯 [SUCCESS] Fernando Silva persistido no cluster de segurança.");
            }
        );
    }
}