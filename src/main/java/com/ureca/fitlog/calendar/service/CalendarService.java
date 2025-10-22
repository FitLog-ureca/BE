package com.ureca.fitlog.calendar.service;

import com.ureca.fitlog.calendar.dto.CalendarResponseDTO;
import com.ureca.fitlog.exercise.dto.ExerciseResponseDTO;
import com.ureca.fitlog.exercise.mapper.ExerciseMapper;
import com.ureca.fitlog.todos.dto.TodoResponseDTO;
import com.ureca.fitlog.todos.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final TodoMapper todoMapper;
    private final ExerciseMapper exerciseMapper;

    public CalendarResponseDTO getDayView(LocalDate date) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        boolean isPast = date.isBefore(today);
        boolean isToday = date.isEqual(today);

        // ✅ 하루 완료 버튼 기준
        boolean dayDone = todoMapper.existsTodosDoneTrueByDate(date);

        boolean useRecordView = isPast || (isToday && dayDone);

        if (useRecordView) {
            List<ExerciseResponseDTO.ExerciseItem> records = exerciseMapper.findCompletedExercisesByDate(date);
            double totalCalories = exerciseMapper.findTotalCaloriesByDate(date);
            return CalendarResponseDTO.record(date, records, totalCalories);
        } else {
            List<TodoResponseDTO.TodoItem> plans = todoMapper.findTodosByDate(date);
            return CalendarResponseDTO.plan(date, plans);
        }
    }
}
