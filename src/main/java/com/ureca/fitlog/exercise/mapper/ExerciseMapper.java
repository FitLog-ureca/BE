package com.ureca.fitlog.exercise.mapper;

import com.ureca.fitlog.exercise.dto.response.ExerciseListResponseDTO;
import com.ureca.fitlog.exercise.dto.response.ExerciseResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ExerciseMapper {

    /** 특정 날짜의 '완료된' 운동 항목 목록 (칼로리 포함) */
    List<ExerciseResponseDTO.ExerciseItem> findCompletedExercisesByDate(
            @Param("date") LocalDate date, @Param("userId") Long userId);
    List<ExerciseResponseDTO.ExerciseItem> findPlannedExercisesByDate(
            @Param("date") LocalDate date, @Param("userId") Long userId);

    /** 특정 날짜의 총 소모 칼로리 */
    double findTotalCaloriesByDate(
            @Param("date") LocalDate date, @Param("userId") Long userId);

    List<ExerciseListResponseDTO.ExerciseList> findExercises(
            @Param("keyword") String keyword,
            @Param("offset") int offset,
            @Param("limit") int limit
    );

    int countExercises(@Param("keyword") String keyword);
}
