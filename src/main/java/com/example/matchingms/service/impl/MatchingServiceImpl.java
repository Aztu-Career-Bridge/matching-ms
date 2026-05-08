package com.example.matchingms.service.impl;

import com.example.matchingms.client.GroqClient;
import com.example.matchingms.client.UserInfoClient;
import com.example.matchingms.client.VacancyClient;
import com.example.matchingms.dto.GroqRequest;
import com.example.matchingms.dto.GroqResponse;
import com.example.matchingms.dto.MatchingResponseDto;
import com.example.matchingms.dto.StudentInfoDto;
import com.example.matchingms.dto.VacancyDto;
import com.example.matchingms.service.MatchingService;
import feign.FeignException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatchingServiceImpl implements MatchingService {

    private final UserInfoClient userInfoClient;
    private final VacancyClient vacancyClient;
    private final GroqClient groqClient;

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.model}")
    private String groqModel;
private final ConcurrentHashMap<Long, MatchingResponseDto> cache = new ConcurrentHashMap<>();

    
    @Override
    public MatchingResponseDto match(Long studentId) {
        try {
            // Check cache first
            if (cache.containsKey(studentId)) {
                log.info("Returning cached result for studentId: {}", studentId);
                return cache.get(studentId);
            }

            // 1. Получаем студента
            log.info("Fetching student with id: {}", studentId);
            var studentResponse = userInfoClient.getById(studentId);
            StudentInfoDto student = studentResponse.getData();
            if (student == null) {
                throw new RuntimeException("Student not found with id: " + studentId);
            }

            // 2. Получаем все вакансии
            log.info("Fetching all vacancies");
            var vacancyResponse = vacancyClient.getAll();
            List<VacancyDto> vacancies = vacancyResponse.getData();
            if (vacancies == null || vacancies.isEmpty()) {
                log.warn("No vacancies found");
                vacancies = List.of();
            }

            // 3. Формируем промпт
            String prompt = buildPrompt(student, vacancies);

            // 4. Отправляем в Groq
            log.info("Sending request to Groq API");
            GroqRequest request = GroqRequest.builder()
                    .model(groqModel)
                    .messages(List.of(
                            GroqRequest.Message.builder()
                                    .role("user")
                                    .content(prompt)
                                    .build()
                    ))
                    .temperature(0.7)
                    .build();

            GroqResponse response = groqClient.generate("Bearer " + groqApiKey, request);
            if (response == null || response.getChoices() == null || response.getChoices().isEmpty()) {
                throw new RuntimeException("Empty response from Groq API");
            }

            // 5. Достаём текст ответа
            String result = response.getChoices()
                    .get(0)
                    .getMessage()
                    .getContent();

            MatchingResponseDto responseDto = new MatchingResponseDto(studentId, result);
            cache.put(studentId, responseDto);
            return responseDto;
        } catch (FeignException e) {
            log.error("Feign client error: status={}, message={}", e.status(), e.getMessage(), e);
            throw new RuntimeException("Failed to communicate with external service: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error during matching process for studentId={}", studentId, e);
            throw new RuntimeException("Matching failed: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(StudentInfoDto student, List<VacancyDto> vacancies) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("Student profile:\n");
        prompt.append("Name: ").append(student.getName()).append(" ").append(student.getSurname()).append("\n");
        prompt.append("University: ").append(student.getUniversity()).append("\n");
        prompt.append("Specialization: ").append(student.getSpecialization()).append("\n");
        prompt.append("Skills: ").append(String.join(", ", student.getSkills())).append("\n\n");

        prompt.append("Available vacancies:\n");
        for (int i = 0; i < vacancies.size(); i++) {
            VacancyDto v = vacancies.get(i);
            prompt.append(i + 1).append(". ").append(v.getTitle())
                    .append(" at ").append(v.getCompany()).append("\n");
            prompt.append("   Location: ").append(v.getLocation()).append("\n");
            prompt.append("   Employment type: ").append(v.getEmploymentType()).append("\n");
            prompt.append("   Experience level: ").append(v.getExperienceLevel()).append("\n");
            prompt.append("   Required skills: ")
                    .append(String.join(", ", v.getRequiredSkills())).append("\n\n");
        }

        prompt.append("For each vacancy:\n");
        prompt.append("1. Calculate match percentage based on student skills\n");
        prompt.append("2. List missing skills\n");
        prompt.append("3. If no vacancy matches above 30%, recommend specific online courses, ");
        prompt.append("internship programs or scholarships\n");
        prompt.append("Respond in English.");

        return prompt.toString();
    }
}
