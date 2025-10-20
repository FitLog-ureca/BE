package com.ureca.fitlog.controller;

import com.ureca.fitlog.dto.TodoRequestDTO;
import com.ureca.fitlog.dto.TodoResponseDTO;
import com.ureca.fitlog.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;
    /** 날짜별 투두 생성 */
    @PostMapping("/create")
    public ResponseEntity<?> createTodo(@RequestBody TodoRequestDTO requestDto) {
        return ResponseEntity.ok(todoService.createTodo(requestDto));
    }

    /** 날짜별 투두 조회 */
    @GetMapping
    public ResponseEntity<TodoResponseDTO> getTodosByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(todoService.getTodosByDate(date));
    }
}
