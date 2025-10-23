package com.ureca.fitlog.todos.controller;

import com.ureca.fitlog.todos.dto.TodoRequestDTO;
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

    /** 운동 완료 버튼 (해당 날짜의 모든 todo_id의 is_done을 true로 전체 변경) */
    @PatchMapping("/date/{date}/done")
    public ResponseEntity<Map<String, Object>> updateTodosDoneStatus(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        // 기본적으로 true로 처리
        boolean isDone = true;

        int updated = todoMapper.updateTodosDoneStatus(date, isDone);

        Map<String, Object> response = new HashMap<>();
        response.put("date", date);
        response.put("isDone", isDone);
        response.put("updatedCount", updated);
        response.put("message", "해당 날짜의 운동이 완료 상태로 변경되었습니다.");

        return ResponseEntity.ok(response);
    }

    /** 개별 세트 완료 (is_completed 변경 — 명시적 true/false만 허용) */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> toggleTodoCompletion(@PathVariable("id") Long todoId) {
        return ResponseEntity.ok(todoService.updateTodoCompletion(todoId));
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

    /** ✅ 세트별 휴식시간 기록 (초 단위) */
    @PatchMapping("/{id}/rest-time")
    public ResponseEntity<Map<String, Object>> updateRestTime(
            @PathVariable("id") Long todoId,
            @RequestBody Map<String, Integer> body) {

        Integer restTime = body.get("restTime");

        // ✅ 예외처리: 값이 없거나 음수일 경우
        if (restTime == null || restTime < 0) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "restTime 값은 0 이상의 정수여야 합니다.");
            return ResponseEntity.badRequest().body(error);
        }

        // ✅ 정상 로직 실행
        return ResponseEntity.ok(todoService.updateRestTime(todoId, restTime));
    }

}
