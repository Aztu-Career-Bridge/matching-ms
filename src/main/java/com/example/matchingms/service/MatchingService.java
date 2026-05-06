package com.example.matchingms.service;

import com.example.matchingms.dto.MatchingResponseDto;

public interface MatchingService {

    MatchingResponseDto match(Long studentId);
}
