package com.ureca.fitlog.todos.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoCompleteResponseDTO {
    private Long todoId;          // 새로 생성된 투두 ID
    private Boolean isCompleted;  // 세트 완료 여부
    private String message;       // 응답 메시지
}



