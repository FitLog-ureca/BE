package com.ureca.fitlog.exercise.service;

import com.ureca.fitlog.auth.mapper.AuthMapper;
import com.ureca.fitlog.common.SecurityUtil;
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

    //  칼로리 계산 기준 상수
    private static final double BASE_WEIGHT = 60.0;          // 기준 체중(kg)
    private static final double BASE_REP_SECONDS = 2.5;      // 기본 1회당 소요 시간(초)
    private static final double TRANSITION_SECONDS = 20.0;   // 세트 간 텀(초)
    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    /** 중량에 따른 rep 시간 보정 */
    private double calculateRepSeconds(Double weight) {
        if (weight == null) return BASE_REP_SECONDS;

        if (weight >= 80) return 4.0;
        if (weight >= 50) return 3.5;
        if (weight >= 30) return 3.0;

        return BASE_REP_SECONDS;
    }

    /**
     * caloriesPerRep는 실제로 MET 계수 값
     */
    private double calculateBurnedCalories(
            double caloriesPerRep,
            int sets,
            int reps,
            Double weight
    ) {
        double repSeconds = calculateRepSeconds(weight);

        double totalSeconds =
                sets * reps * repSeconds
                        + Math.max(sets - 1, 0) * TRANSITION_SECONDS;

        return caloriesPerRep * BASE_WEIGHT * (totalSeconds / 3600.0);
    }

    //  인증 관련 공통 로직
    private Long getCurrentUserId() {
        String loginId = SecurityUtil.getLoginId();
        if (!StringUtils.hasText(loginId)) {
            throw new BusinessException(ExceptionStatus.TODO_AUTH_LOGIN_INFO_NOT_FOUND);
        }

        Long userId = authMapper.findUserIdByLoginId(loginId);
        if (userId == null) {
            throw new BusinessException(ExceptionStatus.TODO_AUTH_USER_NOT_FOUND);
        }

        return userId;
    }

    // 날짜별 운동 조회
    public ExerciseResponseDTO getExercisesByDate(LocalDate date) {
        if (date == null) {
            throw new BusinessException(ExceptionStatus.EXERCISE_VALIDATION_INVALID_DATE);
        }

        Long userId = getCurrentUserId();

        boolean isDone = todoMapper.existsTodosDoneTrueByDate(date, userId) > 0;

        List<ExerciseResponseDTO.ExerciseItem> exercises;
        double totalCalories = 0.0;

        if (isDone) {
            exercises = exerciseMapper.findCompletedExercisesByDate(date, userId);

            for (ExerciseResponseDTO.ExerciseItem item : exercises) {
                double burnedCalories = calculateBurnedCalories(
                        item.getCaloriesPerRep(),   // MET 값
                        item.getSetsNumber(),
                        item.getRepsTarget(),
                        item.getWeight()
                );
                burnedCalories = roundToOneDecimal(burnedCalories);
                item.setBurnedCalories(burnedCalories);
                totalCalories += burnedCalories;
            }
            totalCalories = roundToOneDecimal(totalCalories);
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

//   운동 목록 검색
    public ExerciseListResponseDTO getExercises(String keyword, int page, int size) {
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
