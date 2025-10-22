package com.ureca.fitlog.todos.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class TodoRequestDTO {
    private Long todoId;
    private LocalDate date;       // 운동 날짜
    private Long exerciseId;      // 운동 종목 ID
    private Integer setsNumber;   // 세트 수
    private Integer repsTarget;   // 세트당 목표 횟수 (유산소일 경우 시간)
    private Integer restTime;     // 총 휴식시간 (초 단위)
    private LocalDateTime createdAt;
}
