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
    private Integer repsTarget;   // 세트당 목표 횟수 (유산소일 경우 시간)
    private Integer restTime;     // 총 휴식시간 (초 단위)
    private Boolean isCompleted;  // 개별 세트 완료 여부 - 10/22 17:06 수정
    private Boolean isDone;       // 하루 전체 완료 여부 (운동 완료 버튼) - 10/22 17:06 수정
    private LocalDateTime createdAt;
}
