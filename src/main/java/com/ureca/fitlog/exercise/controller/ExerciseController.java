package com.ureca.fitlog.exercise.controller;

import com.ureca.fitlog.exercise.dto.ExerciseListResponseDTO;
import com.ureca.fitlog.exercise.dto.ExerciseResponseDTO;
import com.ureca.fitlog.exercise.service.ExerciseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exercises")
@Tag(name = "Exercise API", description = "운동 기록 조회 관련 API")
public class ExerciseController {

    private final ExerciseService exerciseService;

    /**
     * 특정 날짜의 운동 계획 or 기록 조회
     * - isDone == false → 운동 계획 목록 (칼로리 관련 정보 X)
     * - isDone == true  → 완료된 운동 기록 + 총 소모 칼로리 표시
     */
    @Operation(
            summary = "운동 기록 조회",
            description = """
                    특정 날짜의 운동 기록 또는 계획을 조회합니다.  
                    - isDone == false : 아직 완료하지 않은 계획 목록  
                    - isDone == true  : 완료된 운동 기록 및 총 칼로리 조회
                    """
    )
    @GetMapping
    public ResponseEntity<ExerciseResponseDTO>  getExercisesByDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        ExerciseResponseDTO response = exerciseService.getExercisesByDate(date);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ExerciseListResponseDTO> searchExercises(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(exerciseService.getExercises(keyword, page, size));
    }
}
