package com.ureca.fitlog.controller;

import com.ureca.fitlog.dto.TodoRequestDTO;
import com.ureca.fitlog.dto.TodoResponseDTO;
import com.ureca.fitlog.service.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

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

    /** 체크 (완료 여부 갱신) */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> updateTodoCompletion(
            @PathVariable("id") Long todoId,
            @RequestParam(defaultValue = "true") Boolean isCompleted) {
        return ResponseEntity.ok(todoService.updateTodoCompletion(todoId, isCompleted));
    }

    /** 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTodoById(@PathVariable("id") Long todoId) {
        return ResponseEntity.ok(todoService.deleteTodoById(todoId));
    }
}
