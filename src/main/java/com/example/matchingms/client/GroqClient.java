package com.example.matchingms.client;

import com.example.matchingms.dto.GroqRequest;
import com.example.matchingms.dto.GroqResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "groq", url = "${groq.api.url}")
public interface GroqClient {

    @PostMapping("/openai/v1/chat/completions")
    GroqResponse generate(
            @RequestHeader("Authorization") String authorization,
            @RequestBody GroqRequest request
    );
}
