package com.ureca.fitlog.exercise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseListResponseDTO {
    private Long exerciseId;              // 운동 ID
    private String name;                  // 운동명
    private Double defaultCaloriesPerSet; // 1회(또는 1분) 기준 칼로리
//    private String unit;                  // 단위 (회 / 분)
}
