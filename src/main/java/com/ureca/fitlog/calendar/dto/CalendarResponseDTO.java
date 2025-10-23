package com.ureca.fitlog.calendar.dto;

import com.ureca.fitlog.exercise.dto.ExerciseResponseDTO;
import com.ureca.fitlog.todos.dto.TodoResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
public class CalendarResponseDTO {

    public enum Mode { PLAN, RECORD } // 계획/기록

    private LocalDate date;
    private Mode mode;
    private double totalCalories;     // RECORD 일 때 의미
    private List<?> items;            // PLAN: TodoItem[], RECORD: ExerciseItem[]
    private String message;

    // 기록 뷰
    public static CalendarResponseDTO record(LocalDate date,
                                             List<ExerciseResponseDTO.ExerciseItem> items,
                                             double totalCalories) {
        CalendarResponseDTO dto = new CalendarResponseDTO();
        dto.setDate(date);
        dto.setMode(Mode.RECORD);
        dto.setItems(items);
        dto.setTotalCalories(totalCalories);
        dto.setMessage(items.isEmpty()
                ? "해당 날짜의 완료된 운동 기록이 없습니다."
                : "운동 기록이 성공적으로 조회되었습니다.");
        return dto;
    }

    // 계획 뷰
    public static CalendarResponseDTO plan(LocalDate date,
                                           List<TodoResponseDTO.TodoItem> items) {
        CalendarResponseDTO dto = new CalendarResponseDTO();
        dto.setDate(date);
        dto.setMode(Mode.PLAN);
        dto.setItems(items);
        dto.setTotalCalories(0);
        dto.setMessage(items.isEmpty()
                ? "해당 날짜의 운동 계획이 없습니다."
                : "운동 계획이 성공적으로 조회되었습니다.");
        return dto;
    }
}
