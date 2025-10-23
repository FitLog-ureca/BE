package com.ureca.fitlog.todos.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TodoResponseDTO {
    private LocalDate date;          // 날짜
    private List<TodoItem> todos;    // 투두 리스트
    private String message;

    @Getter
    @Setter
    public static class TodoItem {
        private Long todoId;
        private Long exerciseId;
        private String exerciseName;
        private Integer setsNumber;
        private Integer repsTarget;
        private Integer restTime;
        private Boolean isCompleted;  // 세트별 완료 여부
        private Boolean isDone;       // ✅ 하루 전체 완료 여부
        private LocalDateTime createdAt;
    }
}
