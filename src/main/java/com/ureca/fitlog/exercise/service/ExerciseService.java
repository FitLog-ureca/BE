package com.ureca.fitlog.exercise.service;

import com.ureca.fitlog.exercise.dto.ExerciseResponseDTO;
import com.ureca.fitlog.exercise.mapper.ExerciseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseMapper exerciseMapper;

    /** 기록(완료) 뷰: 완료 항목 + 총 칼로리 */
    public ExerciseResponseDTO getCompletedRecords(LocalDate date) {
        List<ExerciseResponseDTO.ExerciseItem> records = exerciseMapper.findCompletedExercisesByDate(date);
        double totalCalories = exerciseMapper.findTotalCaloriesByDate(date);

        ExerciseResponseDTO dto = new ExerciseResponseDTO();
        dto.setDate(date);
        dto.setExercises(records);
        dto.setTotalCalories(totalCalories);
        dto.setMessage(records.isEmpty()
                ? "해당 날짜에 완료된 운동 기록이 없습니다."
                : "운동 기록이 성공적으로 조회되었습니다.");
        return dto;
    }
}
