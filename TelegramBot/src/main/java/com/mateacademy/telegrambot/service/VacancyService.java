package com.mateacademy.telegrambot.service;

import com.mateacademy.telegrambot.dto.VacancyDto;
import com.mateacademy.telegrambot.utils.ProficiencyLevel;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VacancyService {

    private final Map<String, VacancyDto> vacancies = new HashMap<>();

    @PostConstruct
    public void init() {
        VacancyDto junVacancy = new VacancyDto(
                "1",
                "Java Junior Developer",
                "Java Core, Java Collection Framework");
        VacancyDto midVacancy = new VacancyDto(
                "2",
                "Java Middle Developer",
                "Spring Boot, Spring Security, Spring Data");
        VacancyDto sinVacancy = new VacancyDto(
                "3",
                "Java Senior Developer",
                "Microservices Patterns, AWS, GCP");

        vacancies.put("1", junVacancy);
        vacancies.put("2", midVacancy);
        vacancies.put("3", sinVacancy);

    }

    public List<VacancyDto> findVacancies(ProficiencyLevel level) {
        return vacancies.values().stream()
                .filter(v -> v.getTitle().toLowerCase().contains(level.name().toLowerCase()))
                .toList();
    }

    public VacancyDto getVacancy(String id) {
        return vacancies.get(id);
    }
}
