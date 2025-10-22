package com.ureca.fitlog.calendar.controller;

import com.ureca.fitlog.calendar.dto.CalendarResponseDTO;
import com.ureca.fitlog.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    private final CalendarService calendarService;

    /** 달력의 하루 단위 통합 조회 엔드포인트 */
    @GetMapping
    public ResponseEntity<CalendarResponseDTO> getDay(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(calendarService.getDayView(date));
    }
}
