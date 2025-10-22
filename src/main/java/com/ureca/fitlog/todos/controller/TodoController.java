package com.ureca.fitlog.todos.controller;

import com.ureca.fitlog.todos.dto.TodoRequestDTO;
import com.ureca.fitlog.todos.dto.TodoResponseDTO;
import com.ureca.fitlog.todos.service.TodoService;
import com.ureca.fitlog.todos.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;
    private final TodoMapper todoMapper;

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

    /** ✅ 운동 완료 버튼 (is_done 전체 변경) */
    @PatchMapping("/done")
    public ResponseEntity<Map<String, Object>> updateTodosDoneStatus(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "true") Boolean isDone) {

        int updated = todoMapper.updateTodosDoneStatus(date, isDone);

        Map<String, Object> response = new HashMap<>();
        response.put("date", date);
        response.put("isDone", isDone);
        response.put("updatedCount", updated);
        response.put("message", isDone
                ? "운동 완료 상태로 변경되었습니다."
                : "운동 완료 상태가 해제되었습니다.");

        return ResponseEntity.ok(response);
    }

    /** 개별 완료 체크 */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> updateTodoCompletion(
            @PathVariable("id") Long todoId,
            @RequestParam(defaultValue = "true") Boolean isCompleted) {
        return ResponseEntity.ok(todoService.updateTodoCompletion(todoId, isCompleted));
    }

    /** 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateTodo(
            @PathVariable("id") Long todoId,
            @RequestBody TodoRequestDTO dto) {
        dto.setTodoId(todoId);
        return ResponseEntity.ok(todoService.updateTodo(dto));
    }

    /** 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteTodoById(@PathVariable("id") Long todoId) {
        return ResponseEntity.ok(todoService.deleteTodoById(todoId));
    }
}
