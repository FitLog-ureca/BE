package com.ureca.fitlog.todos.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoRequestDTO {
    private Long todoId;
    private LocalDate date;       // 운동 날짜
    private Long exerciseId;      // 운동 종목 ID
    private Integer setsNumber;   // 세트 수
    private Integer repsTarget;   // 세트당 목표 횟수
    private Integer restTime;     // 총 휴식시간 (초 단위)
    private boolean isCompleted;  // 개별 세트 완료 여부 (투루리스트 체크)
    private boolean isDone;       // 하루 전체 완료 여부 (운동 완료 버튼)
    private LocalDateTime createdAt;
}
