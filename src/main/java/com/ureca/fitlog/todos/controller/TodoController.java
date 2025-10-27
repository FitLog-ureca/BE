package com.ureca.fitlog.todos.controller;

import com.ureca.fitlog.todos.dto.*;
import com.ureca.fitlog.todos.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/todos")
@Tag(name = "Todos API", description = "Todos 관련 API")
public class TodoController {

    private final TodoService todoService;

    /** 날짜별 투두 생성 */
    @PostMapping("/create")
    @Operation(
            summary = "운동 목표(todos) 생성"
    )
    public ResponseEntity<TodoCreateResponseDTO> createTodo(@RequestBody TodoCreateRequestDTO createRequestDto) {
        return ResponseEntity.ok(todoService.createTodo(createRequestDto));
    }

    /** 운동 완료 상태 토글 (true ↔ false 자동 전환) */
    @PatchMapping("/done/{date}")
    @Operation(
            summary = "하루 운동 완료"
    )
    public ResponseEntity<TodoDoneResponseDTO> toggleTodosDoneStatus(@PathVariable LocalDate date) {

        boolean newStatus = todoService.toggleTodosDoneStatus(date);

        TodoDoneResponseDTO response = TodoDoneResponseDTO.builder()
                .date(date)
                .isDone(newStatus)
                .message(newStatus
                        ? "운동 완료 상태로 변경되었습니다."
                        : "운동 완료 상태가 취소되었습니다.")
                .build();

        return ResponseEntity.ok(response);
    }

    /** 개별 세트 완료 (is_completed 변경 — 명시적 true/false만 허용) */
    @PatchMapping("/complete/{id}")
    @Operation(
            summary = "개별 세트 완료"
    )
    public ResponseEntity<TodoCompleteResponseDTO> toggleTodoCompletion(@PathVariable("id") Long todoId) {
        return ResponseEntity.ok(todoService.updateTodoCompletion(todoId));
    }

//    /** 투두리스트 전체 수정 */
//    @PutMapping("/{id}")
//    public ResponseEntity<TodoCreateResponseDTO> updateTodo(
//            @PathVariable("id") Long todoId,
//            @RequestBody TodoCreateRequestDTO dto) {
//        dto.setTodoId(todoId);
//        return ResponseEntity.ok(todoService.updateTodo(dto));
//    }

    /** 세트당 수행횟수(reps_target)만 수정 */
    @PatchMapping("/reps/{todoId}")
    @Operation(
            summary = "세트당 수행횟수 수정"
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                            { "message": "세트당 횟수가 수정되었습니다." }
                            """))
    )
    public ResponseEntity<Map<String, String>> updateReps(
            @PathVariable Long todoId,
            @RequestParam int repsTarget) {
        todoService.updateTodoRepsOnly(todoId, repsTarget);
        return ResponseEntity.ok(Map.of(
                "message", "세트당 횟수가 수정되었습니다."
        ));
    }

    /** 투두리스트(세트) 삭제 및 sets_number 자동 재정렬 */
    @DeleteMapping("/{todoId}")
    @Operation(
            summary = "운동 목표 삭제"
    )
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                            { "message": "투두리스트가 삭제되고 sets_number가 재정렬되었습니다." }
                            """))
    )
    public ResponseEntity<Map<String, String>> deleteTodoById(@PathVariable Long todoId) {
        todoService.deleteTodoAndReorder(todoId);
        return ResponseEntity.ok(Map.of(
                "message", "투두리스트가 삭제되고 sets_number가 재정렬되었습니다."
        ));
    }
    /** 세트별 휴식시간 기록 (초 단위) */
    @PatchMapping("/rest/{todoId}")
    @Operation(
            summary = "세트별 휴식시간 기록 (초 단위)"
    )
    public ResponseEntity<UpdateRestTimeResponseDTO> updateRestTime(
            @PathVariable Long todoId,
            @RequestBody UpdateRestTimeRequestDTO request
    ) {
        Integer restTime = request.getRestTime();

        // 유효성 검증
        if (restTime == null || restTime < 0 || restTime > 7200) {
            throw new IllegalArgumentException("restTime은 0~7200초 사이여야 합니다.");
        }

        // 서비스 호출
        Map<String, Object> result = todoService.updateRestTime(todoId, restTime);

        UpdateRestTimeResponseDTO response = UpdateRestTimeResponseDTO.builder()
                .todoId((Long) result.get("todoId"))
                .restTime((Integer) result.get("restTime"))
                .message((String) result.get("message"))
                .build();

        return ResponseEntity.ok(response);
    }

    /** 세트별 휴식시간 초기화 */
    @DeleteMapping("/rest/reset/{todoId}")
    @ApiResponse(
            responseCode = "200",
            description = "OK",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(example = """
                            { "todoId": 0, "message": "휴식시간이 초기화되었습니다." }
                            """))
    )
    @Operation(
            summary = "세트별 휴식시간 초기화"
    )
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