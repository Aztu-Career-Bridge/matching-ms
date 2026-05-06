package com.example.matchingms.controller;

import com.example.matchingms.dto.MatchingResponseDto;
import com.example.matchingms.service.MatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/match")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @PostMapping("/{studentId}")
    public ResponseEntity<MatchingResponseDto> match(@PathVariable Long studentId) {
        return ResponseEntity.ok(matchingService.match(studentId));
    }
}