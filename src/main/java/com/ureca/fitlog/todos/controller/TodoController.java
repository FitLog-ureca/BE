package com.ureca.fitlog.todos.controller;

import com.ureca.fitlog.common.dto.ApiMessageResponse;
import com.ureca.fitlog.todos.dto.request.TodoCreateRequestDTO;
import com.ureca.fitlog.todos.dto.request.UpdateRestTimeRequestDTO;
import com.ureca.fitlog.todos.dto.request.UpdateTodoRecordRequestDTO;
import com.ureca.fitlog.todos.dto.response.TodoCompleteResponseDTO;
import com.ureca.fitlog.todos.dto.response.TodoCreateResponseDTO;
import com.ureca.fitlog.todos.dto.response.TodoDoneResponseDTO;
import com.ureca.fitlog.todos.dto.response.UpdateRestTimeResponseDTO;
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

    /**
     * 운동 항목 추가 버튼으로 투두 생성
     * - 항상 첫 번째 세트(sets_number = 1)로 생성됨
     * - workout_id, sets_number는 서버에서 자동 처리
     */
    @PostMapping
    @Operation(
            summary = "운동 항목(todo) 생성",
            description = """
                운동 항목을 생성합니다.
                - 항상 첫 번째 세트(sets_number = 1)로 생성됩니다.
                - workout_id는 서버에서 자동으로 생성됩니다.
                """
    )
    @ApiResponse(responseCode = "200", description = "운동 항목 생성 성공")
    public ResponseEntity<TodoCreateResponseDTO> createWorkout(
            @RequestBody TodoCreateRequestDTO request
    ) {
        return ResponseEntity.ok(todoService.createWorkout(request));
    }


    /** 세트 추가 버튼으로 투두 생성 - sets_number가 기존의 세트 항목 수를 고려하여 증가 */
    @PostMapping("/{todoId}/sets")
    @Operation(
            summary = "세트 추가",
            description = "기존 운동 항목에 새로운 세트를 추가합니다."
    )
    @ApiResponse(responseCode = "200", description = "세트 추가 성공")
    public ResponseEntity<TodoCreateResponseDTO> addSet(
            @PathVariable Long todoId
    ) {
        return ResponseEntity.ok(todoService.addSet(todoId));
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

    /** 세트 기록 수정 */
    @PatchMapping("/record/{todoId}")
    @Operation(summary = "세트 기록 수정 (reps + weight)")
    public ResponseEntity<Map<String, String>> updateTodoRecord(
            @PathVariable Long todoId,
            @RequestBody UpdateTodoRecordRequestDTO request
    ) {
        todoService.updateTodoRecord(todoId, request);
        return ResponseEntity.ok(Map.of(
                "message", "세트 기록이 수정되었습니다."
        ));
    }


    /** 투두리스트(세트 항목) 삭제 및 sets_number 자동 재정렬 */
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

    /** 투두리스트(운동 항목) 삭제 */
    @DeleteMapping("/workouts/{workoutId}")
    public ResponseEntity<ApiMessageResponse> deleteWorkout(@PathVariable Long workoutId) {
        todoService.deleteWorkout(workoutId);
        return ResponseEntity.ok(
                new ApiMessageResponse("운동 항목이 삭제되었습니다.")
        );
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
//        Integer restTime = request.getRestTime();
//
//        // 유효성 검증
//        if (restTime == null || restTime < 0 || restTime > 7200) {
//            throw new IllegalArgumentException("restTime은 0~7200초 사이여야 합니다.");
//        }

        // 서비스 호출
//        Map<String, Object> result = todoService.updateRestTime(todoId, restTime);
        Map<String, Object> result = todoService.updateRestTime(todoId, request.getRestTime());
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
    public ResponseEntity<Map<String, Object>> resetRestTime(@PathVariable Long todoId) {
        todoService.resetRestTime(todoId);

        return ResponseEntity.ok(Map.of(
                "todoId", todoId,
                "message", "휴식시간이 초기화되었습니다."
        ));
    }

}