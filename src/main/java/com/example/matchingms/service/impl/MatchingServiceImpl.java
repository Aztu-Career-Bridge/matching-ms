package com.example.matchingms.service.impl;

import com.example.matchingms.client.GeminiClient;
import com.example.matchingms.client.UserInfoClient;
import com.example.matchingms.client.VacancyClient;
import com.example.matchingms.dto.GeminiRequest;
import com.example.matchingms.dto.GeminiResponse;
import com.example.matchingms.dto.MatchingResponseDto;
import com.example.matchingms.dto.StudentInfoDto;
import com.example.matchingms.dto.VacancyDto;
import com.example.matchingms.service.MatchingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchingServiceImpl implements MatchingService {

    private final UserInfoClient userInfoClient;
    private final VacancyClient vacancyClient;
    private final GeminiClient geminiClient;

    @Value("${gemini.api.key}")
    private String geminiApiKey;
    @Override
    public MatchingResponseDto match(Long studentId) {

        // 1. Получаем студента
        StudentInfoDto student = userInfoClient.getById(studentId);

        // 2. Получаем все вакансии
        List<VacancyDto> vacancies = vacancyClient.getAll();

        // 3. Формируем промпт
        String prompt = buildPrompt(student, vacancies);

        // 4. Отправляем в Gemini
        GeminiRequest request = new GeminiRequest(
                List.of(new GeminiRequest.Content(
                        List.of(new GeminiRequest.Part(prompt))
                ))
        );

        GeminiResponse response = geminiClient.generate(geminiApiKey, request);

        // 5. Достаём текст ответа
        String result = response.getCandidates()
                .get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();

        return new MatchingResponseDto(studentId, result);
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
