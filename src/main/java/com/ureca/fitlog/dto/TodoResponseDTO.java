package com.ureca.fitlog.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TodoResponseDTO {
    private LocalDate date;          // 날짜
    private List<TodoItem> todos;    // 투두 목록
    private String message;          // 응답 메시지

    @Getter
    @Setter
    public static class TodoItem {
        private Long todoId;         // 투두 ID
        private String exerciseName; // 운동 이름
        private Integer setsNumber;  // 세트 수
        private Integer repsTarget;  // 세트당 목표 횟수 (또는 유산소 시간)
        private Integer restTime;    // 총 휴식시간
        private Boolean isCompleted; // 완료 여부
    }
}
