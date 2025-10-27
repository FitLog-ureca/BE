package com.ureca.fitlog.exercise.service;

import com.ureca.fitlog.auth.mapper.AuthMapper;
import com.ureca.fitlog.exercise.dto.ExerciseListResponseDTO;
import com.ureca.fitlog.exercise.dto.ExerciseResponseDTO;
import com.ureca.fitlog.exercise.mapper.ExerciseMapper;
import com.ureca.fitlog.todos.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseMapper exerciseMapper;
    private final TodoMapper todoMapper; // isDone 판단용
    private final AuthMapper authMapper;

    public ExerciseResponseDTO getExercisesByDate(LocalDate date) {

        String loginId = com.ureca.fitlog.common.SecurityUtil.getLoginId();
        if (loginId == null) {
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }

        Long userId = authMapper.findUserIdByLoginId(loginId);
        if (userId == null) {
            throw new IllegalStateException("사용자 정보를 찾을 수 없습니다.");
        }

        // 오늘 운동 완료 여부 확인
        boolean isDone = todoMapper.existsTodosDoneTrueByDate(date) > 0;

        // 운동 목록 조회
        List<ExerciseResponseDTO.ExerciseItem> exercises;
        double totalCalories = 0.0;

        if (isDone) {
            exercises = exerciseMapper.findCompletedExercisesByDate(date, userId);
            totalCalories = exerciseMapper.findTotalCaloriesByDate(date, userId);
        } else {
            exercises = exerciseMapper.findPlannedExercisesByDate(date, userId);
        }

        // 답 DTO 조합 (builder 부분)
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
    /** 운동목록 list */
    public ExerciseListResponseDTO getExercises(String keyword, int page, int size) {
        int offset = page * size;

        List<ExerciseListResponseDTO.ExerciseList> exercises =
                exerciseMapper.findExercises(keyword, offset, size);
        int totalCount = exerciseMapper.countExercises(keyword);

        return ExerciseListResponseDTO.builder()
                .page(page)
                .size(size)
                .totalCount(totalCount)
                .totalPages((int) Math.ceil((double) totalCount / size))
                .exercises(exercises)
                .build();
    }
}
