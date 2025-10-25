package com.ureca.fitlog.exercise.service;

import com.ureca.fitlog.exercise.dto.ExerciseResponseDTO;
import com.ureca.fitlog.exercise.mapper.ExerciseMapper;
import com.ureca.fitlog.todos.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseMapper exerciseMapper;
    private final TodoMapper todoMapper; // isDone 판단용

    public ExerciseResponseDTO getExercisesByDate(LocalDate date) {

        // 1️⃣ 오늘 운동 완료 여부 확인
        boolean isDone = todoMapper.existsTodosDoneTrueByDate(date) > 0;

        // 2️⃣ 운동 목록 조회
        List<ExerciseResponseDTO.ExerciseItem> exercises;
        double totalCalories = 0.0;

        if (isDone) {
            exercises = exerciseMapper.findCompletedExercisesByDate(date);
            totalCalories = exerciseMapper.findTotalCaloriesByDate(date);
        } else {
            exercises = exerciseMapper.findPlannedExercisesByDate(date);
        }

        // 3️⃣ 응답 DTO 조합 (이게 바로 builder 부분)
        return ExerciseResponseDTO.builder()
                .date(date)
                .isDone(isDone)
                .exercises(exercises)
                .totalCalories(totalCalories)
                .message(isDone
                        ? "운동 기록이 성공적으로 조회되었습니다."
                        : "오늘의 운동 계획이 조회되었습니다.")
                .build();
    }
}
