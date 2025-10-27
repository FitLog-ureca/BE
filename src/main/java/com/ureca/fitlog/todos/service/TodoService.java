package com.ureca.fitlog.todos.service;

import com.ureca.fitlog.todos.dto.request.TodoCreateRequestDTO;
import com.ureca.fitlog.todos.dto.response.TodoCompleteResponseDTO;
import com.ureca.fitlog.todos.dto.response.TodoCreateResponseDTO;
import com.ureca.fitlog.todos.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoMapper todoMapper;
    private final com.ureca.fitlog.auth.mapper.AuthMapper authMapper;

    /**
     * 현재 로그인한 사용자의 userId를 가져오는 공통 메서드
     */
    private Long getCurrentUserId() {
        String loginId = com.ureca.fitlog.common.SecurityUtil.getLoginId();
        if (loginId == null) {
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }

        var user = authMapper.findById(loginId);
        if (user == null) {
            throw new IllegalStateException("사용자 정보를 찾을 수 없습니다.");
        }

        return user.getUserId();
    }

    /** Todo 생성 (세트번호 자동 증가) */
    @Transactional
    public TodoCreateResponseDTO createTodo(TodoCreateRequestDTO dto) {
        Long userId = getCurrentUserId();
        dto.setUserId(userId);

        int currentCount = todoMapper.countSetsByDateAndExercise(dto.getDate(), dto.getExerciseId(), userId);
        int nextSetNumber = currentCount + 1;
        dto.setSetsNumber(nextSetNumber);

        todoMapper.insertTodo(dto);

        return TodoCreateResponseDTO.builder()
                .todoId(dto.getTodoId())
                .exerciseId(dto.getExerciseId())
                .setsNumber(dto.getSetsNumber())
                .repsTarget(dto.getRepsTarget())
                .date(dto.getDate())
                .isCompleted(false)
                .message("투두가 성공적으로 생성되었습니다.")
                .build();
    }

    /** 개별 세트 완료 토글 (todoId만으로 true/false 자동 반전) */
    public TodoCompleteResponseDTO updateTodoCompletion(Long todoId) {
        Long userId = getCurrentUserId();

        Boolean currentStatus = todoMapper.getIsCompletedById(todoId, userId);
        if (currentStatus == null) {
            throw new IllegalArgumentException("해당 투두를 찾을 수 없거나 권한이 없습니다.");
        }

        Boolean newStatus = !currentStatus;
        int updated = todoMapper.updateTodoCompletion(todoId, userId, newStatus);

        if (updated == 0) {
            throw new IllegalArgumentException("해당 투두를 찾을 수 없거나 권한이 없습니다.");
        }

        return TodoCompleteResponseDTO.builder()
                .todoId(todoId)
                .isCompleted(newStatus)
                .message(newStatus ? "세트가 완료 처리되었습니다." : "세트 완료가 해제되었습니다.")
                .build();
    }

    /** 현재 is_done 상태를 반전시켜 저장 */
    @Transactional
    public boolean toggleTodosDoneStatus(LocalDate date) {
        Long userId = getCurrentUserId();

        // 현재 상태 조회 (해당 사용자의 투두만)
        boolean currentStatus = todoMapper.existsTodosDoneTrueByDate(date, userId) > 0;

        // 반전된 상태로 업데이트
        boolean newStatus = !currentStatus;
        todoMapper.updateTodosDoneStatus(date, userId, newStatus);

        return newStatus;
    }

    /** 세트당 수행횟수(reps_target)만 수정 */
    @Transactional
    public void updateTodoRepsOnly(Long todoId, int repsTarget) {
        Long userId = getCurrentUserId();
        int updated = todoMapper.updateTodoRepsOnly(todoId, userId, repsTarget);
        if (updated == 0) {
            throw new IllegalArgumentException("해당 투두를 찾을 수 없거나 권한이 없습니다.");
        }
    }

    /** 투두 삭제 */
    public Map<String, Object> deleteTodoById(Long todoId) {
        Long userId = getCurrentUserId();
        int deleted = todoMapper.deleteTodoById(todoId, userId);
        Map<String, Object> response = new HashMap<>();
        if (deleted > 0) {
            response.put("todoId", todoId);
            response.put("message", "TodoList가 성공적으로 삭제되었습니다.");
        } else {
            throw new IllegalArgumentException("해당 투두를 찾을 수 없거나 권한이 없습니다.");
        }
        return response;
    }

    /** 투두 삭제 후 sets_number 재정렬 */
    @Transactional
    public void deleteTodoAndReorder(Long todoId) {
        Long userId = getCurrentUserId();
        // 삭제 대상의 date, exercise_id 조회 (본인 소유 확인)
        Map<String, Object> info = todoMapper.findDateAndExerciseIdByTodoId(todoId, userId);
        if (info == null || info.isEmpty()) {
            throw new IllegalArgumentException("해당 투두를 찾을 수 없거나 권한이 없습니다.");
        }

        Object dateObj = info.get("date");
        LocalDate date = (dateObj instanceof LocalDate)
                ? (LocalDate) dateObj
                : ((Date) dateObj).toLocalDate();

        Long exerciseId = ((Number) info.get("exercise_id")).longValue();

        // 삭제
        todoMapper.deleteTodoById(todoId, userId);
        // 임시 음수화 (UNIQUE 제약 피하기) - 해당 사용자의 투두만
        todoMapper.tempNegateSetsNumbers(date, exerciseId, userId);
        // 세트번호 재정렬 - 해당 사용자의 투두만
        todoMapper.reorderSetsNumbers(date, exerciseId, userId);
    }

    /** 휴식 시간 기록 */
    public Map<String, Object> updateRestTime(Long todoId, Integer restTime) {
        Long userId = getCurrentUserId();

        // 해당 todo가 본인 소유인지 검증하면서 업데이트
        int updated = todoMapper.updateRestTime(todoId, userId, restTime);

        if (updated == 0) {
            throw new IllegalArgumentException("해당 투두를 찾을 수 없거나 권한이 없습니다.");
        }

        // 성공 응답
        Map<String, Object> response = new HashMap<>();
        response.put("todoId", todoId);
        response.put("restTime", restTime);
        response.put("message", "휴식시간이 업데이트되었습니다.");

        return response;
    }

    /** 휴식시간 초기화 */
    public void resetRestTime(Long todoId) {
        Long userId = getCurrentUserId();

        // 해당 todo가 본인 소유인지 확인하며 초기화
        int updated = todoMapper.resetRestTime(todoId, userId);
        if (updated == 0) {
            throw new IllegalArgumentException("해당 투두를 찾을 수 없거나 권한이 없습니다.");
        }
    }
}