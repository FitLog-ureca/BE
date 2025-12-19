package com.ureca.fitlog.exercise.dto.response;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseListResponseDTO {
    private int page;               // 현재 페이지
    private int size;               // 페이지당 데이터 수
    private int totalCount;         // 전체 운동 개수
    private int totalPages;         // 전체 페이지 수
    private List<ExerciseListResponseDTO.ExerciseList> exercises;

    @Getter
    @Setter
    public static class ExerciseList {
        private Long exerciseId;
        private String name;
        private Double defaultCaloriesPerSet; // 기본 MET 계수
        private String unit;                  // 단위 (회 / 분)
    }
}
