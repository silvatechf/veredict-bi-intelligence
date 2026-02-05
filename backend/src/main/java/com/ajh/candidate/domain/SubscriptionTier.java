package com.ajh.candidate.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubscriptionTier {
    FREE(3),    // 3 IA calls per day
    PRO(1000);  // Practically unlimited

    private final int dailyLimit;
}