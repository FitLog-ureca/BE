package com.ureca.fitlog.exercise.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class ExerciseResponseDTO {
    private LocalDate date;                           // 조회 날짜
    private List<ExerciseItem> exercises;             // 완료된 운동 목록
    private double totalCalories;                     // 해당 날짜 총 소모 칼로리
    private String message;

    @Getter @Setter
    public static class ExerciseItem {
        private Long todoId;                          // 투두 ID (참조용)
        private Long exerciseId;                      // 운동 ID
        private String exerciseName;                  // 운동 이름
        private Integer setsNumber;                   // 세트 수
        private Integer repsTarget;                   // 세트당 횟수/시간
        private Integer restTime;                     // 휴식(초)
        private Boolean isCompleted;                  // 완료 여부
        private Double caloriesPerRep;                // 1회(혹은 1단위)당 칼로리
        private Double totalCalories;                 // 해당 항목 총 칼로리 = sets*reps*cal/rep
    }
}
