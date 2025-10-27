package com.ureca.fitlog.todos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoCreateRequestDTO {
    @Schema(hidden = true)
    private Long todoId;
    @Schema(hidden = true)
    private Long userId;
    private LocalDate date;       // 운동 날짜
    private Long exerciseId;      // 운동 종목 ID
    private Integer setsNumber;   // 세트 수
    private Integer repsTarget;   // 세트당 목표 횟수
    private Integer restTime;     // 총 휴식시간 (초 단위)
}
