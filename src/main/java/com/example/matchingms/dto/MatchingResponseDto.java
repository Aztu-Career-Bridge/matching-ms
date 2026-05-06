package com.example.matchingms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchingResponseDto {
    private Long studentId;
    private String result;
}
