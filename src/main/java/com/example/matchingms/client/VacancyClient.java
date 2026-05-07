package com.example.matchingms.client;

import com.example.matchingms.dto.VacancyDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "vacancy-ms", url = "${vacancy-ms.url}")
public interface VacancyClient {

    @GetMapping("/api/v1/vacancies")
    List<VacancyDto> getAll();
}
