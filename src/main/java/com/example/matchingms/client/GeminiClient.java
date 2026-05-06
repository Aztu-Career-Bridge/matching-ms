package com.example.matchingms.client;

import com.example.matchingms.dto.GeminiRequest;
import com.example.matchingms.dto.GeminiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "gemini", url = "${gemini.api.url}")
public interface GeminiClient {

    @PostMapping("/v1beta/models/gemini-2.0-flash:generateContent")
    GeminiResponse generate(
            @RequestParam("key") String apiKey,
            @RequestBody GeminiRequest request
    );
}