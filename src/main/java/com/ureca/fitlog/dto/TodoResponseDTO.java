package com.ureca.fitlog.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TodoResponseDTO {
    private LocalDate date; // 날짜
    private String calendarStatus; // 캘린더 상태
    private List<TodoItem> todos; // 투두 목록
    private String message;

    @Getter
    @Setter
    public static class TodoItem {
        private Long todoId; // 투두 id
        private String exerciseName; // 운동 이름
        private Integer setsTarget; // 목표 세트 수
        private Integer repsTarget; // 목표 세트 당 횟수
        private Double caloriesTarget; // 목표 소모 칼로리
        private Boolean isCompleted; // 완료 여부
    }
}