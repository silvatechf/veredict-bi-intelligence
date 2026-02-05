package com.ajh.common.infrastructure.scheduler;

import com.ajh.candidate.infrastructure.persistence.repository.JpaUsageStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@Slf4j
@RequiredArgsConstructor
public class QuotaResetScheduler {

    private final JpaUsageStatsRepository usageRepository;

    /**
     * Google Standard - Maintenance Job:
     * Este método roda automaticamente todos os dias à meia-noite (00:00:00).
     * "0 0 0 * * *" = Cron expression para Meia-noite.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void resetDailyQuotas() {
        log.info("🧹 [MAINTENANCE] Starting daily quota reset procedure...");
        
        try {
            // No Padrão Google, em vez de deletar, poderíamos arquivar. 
            // Para nosso MVP, vamos limpar os registros de ontem para liberar espaço e resetar o contador.
            usageRepository.deleteAll(); 
            
            log.info("✅ [MAINTENANCE] All daily quotas have been reset for {}. Users are ready for a new day!", LocalDate.now());
        } catch (Exception e) {
            log.error("❌ [MAINTENANCE] Failed to reset quotas: {}", e.getMessage());
        }
    }
}