package com.mateacademy.telegrambot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VacancyDto {

    private String id;
    private String title;
    private String shortDescription;
}
