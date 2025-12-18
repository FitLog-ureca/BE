package com.ureca.fitlog.todos.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TodoInsertDTO {

    private Long todoId;        // useGeneratedKeys 결과
    private Long userId;        // 서버에서 세팅
    private Long workoutId;     // 서버에서 세팅
    private Long exerciseId;
    private LocalDate date;
    private Integer setsNumber;
    private Integer repsTarget;
    private Double weight;
    private Integer restTime;
}
