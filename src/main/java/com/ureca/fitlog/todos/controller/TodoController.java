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
    @PatchMapping("/done/{date}")
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
    @PatchMapping("/complete/{id}")
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
    @PatchMapping("/rest/{todoId}")
    public ResponseEntity<?> updateRestTime(
            @PathVariable Long todoId,
            @RequestBody Map<String, Integer> body
    ) {
        Integer restTime = body.get("restTime");

        // 1️⃣ 유효성 검증
        if (restTime == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "error", "INVALID_ARGUMENT",
                    "message", "restTime은 필수 값입니다."
            ));
        }
        if (restTime < 0 || restTime > 7200) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", 400,
                    "error", "INVALID_ARGUMENT",
                    "message", "restTime은 0~7200초 사이여야 합니다."
            ));
        }

        // 2️⃣ 서비스 실행
        try {
            todoService.updateRestTime(todoId, restTime);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", 401,
                    "error", "UNAUTHORIZED",
                    "message", e.getMessage()
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of(
                    "status", 403,
                    "error", "FORBIDDEN",
                    "message", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "status", 404,
                    "error", "NOT_FOUND",
                    "message", e.getMessage()
            ));
        }

        // 3️⃣ 성공 응답
        return ResponseEntity.ok(Map.of(
                "todoId", todoId,
                "restTime", restTime,
                "message", "휴식시간이 업데이트되었습니다."
        ));
    }

    /** ✅ 세트별 휴식시간 초기화 */
    @DeleteMapping("/rest/reset/{todoId}")
    public ResponseEntity<?> resetRestTime(@PathVariable Long todoId) {
        try {
            todoService.resetRestTime(todoId);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(401).body(Map.of(
                    "status", 401,
                    "error", "UNAUTHORIZED",
                    "message", e.getMessage()
            ));
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(Map.of(
                    "status", 403,
                    "error", "FORBIDDEN",
                    "message", e.getMessage()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "status", 404,
                    "error", "NOT_FOUND",
                    "message", e.getMessage()
            ));
        }

        return ResponseEntity.ok(Map.of(
                "todoId", todoId,
                "message", "휴식시간이 초기화되었습니다."
        ));
    }
}
