package com.ureca.fitlog.todos.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoDoneResponseDTO {
    private LocalDate date;       // 날짜
    private Boolean isDone;  // 세트 완료 여부
    private String message;       // 응답 메시지
}
