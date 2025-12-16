package com.ureca.fitlog.todos.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoCreateResponseDTO {
    private Long todoId;          // 새로 생성된 투두 ID
    private LocalDate date;       // 날짜
    private Long exerciseId;      // 운동 ID
    private Integer setsNumber;   // 자동 세트 번호
    private Integer repsTarget;   // 목표 횟수
    private Double weight;
    private Integer restTime;
    private Boolean isCompleted;  // 세트 완료 여부
    private String message;       // 응답 메시지
}
