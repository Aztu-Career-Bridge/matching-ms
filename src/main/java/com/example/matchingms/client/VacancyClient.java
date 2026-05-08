package com.example.matchingms.client;

import com.example.matchingms.config.FeignConfig;
import com.example.matchingms.dto.ApiResponseDto;
import com.example.matchingms.dto.VacancyDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "vacancy-ms", url = "${vacancy-ms.url}", configuration = FeignConfig.class)
public interface VacancyClient {

    @GetMapping("/api/v1/vacancies")
    ApiResponseDto<List<VacancyDto>> getAll();
}
