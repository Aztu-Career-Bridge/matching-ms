package com.example.matchingms.dto;

import java.util.List;
import lombok.Data;

@Data
public class VacancyDto {
    private String title;
    private String company;
    private String location;
    private String employmentType;
    private String experienceLevel;
    private List<String> requiredSkills;
}
