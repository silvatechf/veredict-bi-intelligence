package com.ajh.common.application.port.out;

public interface NotificationPort {
    void sendTopTalentAlert(String candidateName, String candidateEmail, Double score, String jobTitle);
}