package com.ureca.fitlog.exercise.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter @Builder
public class ExerciseResponseDTO {
    private LocalDate date;                           // 조회 날짜
    private Boolean isDone;                           // 운동 완료 여부
    private List<ExerciseItem> exercises;             // 완료된 운동 목록
    private double totalCalories;                     // 하루 총 칼로리 합계
    private String message;

    @Getter @Setter
    public static class ExerciseItem {
        private Long todoId;                          // 투두 ID (참조용)
        private Long workoutId;
        private Long exerciseId;                      // 운동 ID
        private String exerciseName;                  // 운동 이름
        private Integer setsNumber;                   // 세트 수
        private Integer repsTarget;                   // 세트당 횟수/시간
        private Double weight;                        // 중량
        private Integer restTime;                     // 휴식(초)
        private Boolean isCompleted;                  // 세트 완료 여부
        private Double caloriesPerRep;                // MET 계수
        private Double burnedCalories;                // 세트 당 소모 칼로리
    }
}
