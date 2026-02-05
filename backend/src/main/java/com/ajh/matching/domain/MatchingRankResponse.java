package com.ajh.matching.domain;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class MatchingRankResponse {
    private Long candidateId;
    private int totalJobsAnalyzed;
    private List<MatchResult> rankings;
}