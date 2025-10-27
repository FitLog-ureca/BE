package com.ureca.fitlog.exercise.service;

import com.ureca.fitlog.auth.mapper.AuthMapper;
import com.ureca.fitlog.common.exception.BusinessException;
import com.ureca.fitlog.common.exception.ExceptionStatus;
import com.ureca.fitlog.exercise.dto.response.ExerciseListResponseDTO;
import com.ureca.fitlog.exercise.dto.response.ExerciseResponseDTO;
import com.ureca.fitlog.exercise.mapper.ExerciseMapper;
import com.ureca.fitlog.todos.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExerciseService {

    private final ExerciseMapper exerciseMapper;
    private final TodoMapper todoMapper;
    private final AuthMapper authMapper;

    /**
     * 현재 로그인한 사용자의 userId를 가져오는 공통 메서드
     */
    private Long getCurrentUserId() {
        String loginId = com.ureca.fitlog.common.SecurityUtil.getLoginId();
        if (!StringUtils.hasText(loginId)) {
            throw new BusinessException(ExceptionStatus.TODO_AUTH_LOGIN_INFO_NOT_FOUND);
        }

        Long userId = authMapper.findUserIdByLoginId(loginId);
        if (userId == null) {
            throw new BusinessException(ExceptionStatus.TODO_AUTH_USER_NOT_FOUND);
        }

        return userId;
    }

    /**
     * 특정 날짜의 운동 계획 or 기록 조회
     */
    public ExerciseResponseDTO getExercisesByDate(LocalDate date) {
        // 날짜 유효성 검증
        if (date == null) {
            throw new BusinessException(ExceptionStatus.EXERCISE_VALIDATION_INVALID_DATE);
        }

        Long userId = getCurrentUserId();

        // 오늘 운동 완료 여부 확인 (userId 포함)
        boolean isDone = todoMapper.existsTodosDoneTrueByDate(date, userId) > 0;

        // 운동 목록 조회
        List<ExerciseResponseDTO.ExerciseItem> exercises;
        double totalCalories = 0.0;

        if (isDone) {
            exercises = exerciseMapper.findCompletedExercisesByDate(date, userId);
            totalCalories = exerciseMapper.findTotalCaloriesByDate(date, userId);
        } else {
            exercises = exerciseMapper.findPlannedExercisesByDate(date, userId);
        }


        return ExerciseResponseDTO.builder()
                .date(date)
                .isDone(isDone)
                .exercises(exercises != null ? exercises : List.of())
                .totalCalories(totalCalories)
                .message(isDone
                        ? "운동 기록이 성공적으로 조회되었습니다."
                        : "오늘의 운동 계획이 조회되었습니다.")
                .build();
    }

    /**
     * 운동 목록 검색 (페이징)
     */
    public ExerciseListResponseDTO getExercises(String keyword, int page, int size) {
        // 페이징 유효성 검증
        if (page < 0) {
            throw new BusinessException(ExceptionStatus.EXERCISE_VALIDATION_INVALID_PAGE);
        }
        if (size < 1) {
            throw new BusinessException(ExceptionStatus.EXERCISE_VALIDATION_INVALID_SIZE);
        }

        int offset = page * size;

        List<ExerciseListResponseDTO.ExerciseList> exercises =
                exerciseMapper.findExercises(keyword, offset, size);
        int totalCount = exerciseMapper.countExercises(keyword);

        return ExerciseListResponseDTO.builder()
                .page(page)
                .size(size)
                .totalCount(totalCount)
                .totalPages((int) Math.ceil((double) totalCount / size))
                .exercises(exercises != null ? exercises : List.of())
                .build();
    }
}