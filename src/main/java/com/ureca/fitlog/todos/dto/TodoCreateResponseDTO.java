package com.ureca.fitlog.todos.dto;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoCreateResponseDTO {
    private Long todoId;          // 새로 생성된 투두 ID
    private Long exerciseId;      // 운동 ID
    private Integer setsNumber;   // 자동 세트 번호
    private Integer repsTarget;   // 목표 횟수
    private LocalDate date;       // 날짜
    private Boolean isCompleted;  // 세트 완료 여부
    private String message;       // 응답 메시지
}
