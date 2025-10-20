package com.ureca.fitlog.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TodoRequestDTO {
    private LocalDate date; // 운동 날짜
    private Long exerciseId; // 운동 종목 id
    private Integer setsTarget; // 목표 세트 수
    private Integer repsTarget; // 목표 세트당 횟수
    private Double caloriesTarget; // 목표 소모 칼로리
    private Boolean isCompleted; // 완료 여부
}
