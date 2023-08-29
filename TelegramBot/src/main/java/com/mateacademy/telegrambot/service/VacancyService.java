package com.mateacademy.telegrambot.service;

import com.mateacademy.telegrambot.dto.VacancyDto;
import com.mateacademy.telegrambot.utils.ProficiencyLevel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class VacancyService {

    private final Map<String, VacancyDto> vacancies = new HashMap<>();

    @Autowired
    private VacanciesReaderService vacanciesReaderService;

    @PostConstruct
    public void init() {
        List<VacancyDto> list = vacanciesReaderService.getVacanciesFromFile("vacancies.csv");
        for (VacancyDto vacancy : list) {
            vacancies.put(vacancy.getId(), vacancy);
        }
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
