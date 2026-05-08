package com.example.matchingms.client;

import com.example.matchingms.dto.ApiResponseDto;
import com.example.matchingms.dto.StudentInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-info-ms", url = "${user-info-ms.url}")
public interface UserInfoClient {


    @GetMapping("/api/v1/students/{id}")
    ApiResponseDto<StudentInfoDto> getById(@PathVariable Long id);
}