package com.example.matchingms.dto;

import java.util.List;
import lombok.Data;

@Data
public class StudentInfoDto {
    private String name;
    private String surname;
    private String email;
    private String university;
    private String specialization;
    private Integer graduationYear;
    private List<String> skills;
}
