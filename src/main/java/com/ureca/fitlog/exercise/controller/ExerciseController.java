package com.ureca.fitlog.exercise.controller;

import com.ureca.fitlog.exercise.dto.ExerciseResponseDTO;
import com.ureca.fitlog.exercise.service.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/exercises")
public class ExerciseController {

    private final ExerciseService exerciseService;

    /** 특정 날짜 완료 기록 조회(단독 호출용) */
    @GetMapping
    public ResponseEntity<ExerciseResponseDTO> getCompletedByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(exerciseService.getCompletedRecords(date));
    }
}
