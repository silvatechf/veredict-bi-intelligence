package com.ajh.common.infrastructure.notification;

import com.ajh.common.application.port.out.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ConsoleNotificationAdapter implements NotificationPort {

    @Override
    public void sendTopTalentAlert(String name, String email, Double score, String jobTitle) {
        log.warn("📧 [EMAIL-DISPATCHER] Preparing email to Recruiter...");
        log.info("---------------------------------------------------------");
        log.info("SUBJECT: 🚨 Top Talent Found for {}!", jobTitle);
        log.info("BODY: Hello! Our AI identified {} as a high-potential match ({}%)", name, (int)(score * 100));
        log.info("Contact: {}", email);
        log.info("---------------------------------------------------------");
        log.info("✅ [EMAIL-DISPATCHER] Email sent successfully to recruitment-team@ajh.com");
    }
}