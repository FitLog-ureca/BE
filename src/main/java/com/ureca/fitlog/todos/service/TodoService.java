package com.ureca.fitlog.todos.service;

import com.ureca.fitlog.todos.dto.TodoCreateResponseDTO;
import com.ureca.fitlog.todos.dto.TodoRequestDTO;
import com.ureca.fitlog.todos.dto.TodoResponseDTO;
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

    /** ✅ Todo 생성 (세트번호 자동 증가) */
    @Transactional
    public TodoCreateResponseDTO createTodo(TodoRequestDTO dto) {
        // 1️⃣ 해당 날짜+운동 종목의 현재 세트 수 조회
        int currentCount = todoMapper.countSetsByDateAndExercise(dto.getDate(), dto.getExerciseId());

        // 2️⃣ 다음 세트 번호 계산
        int nextSetNumber = currentCount + 1;
        dto.setSetsNumber(nextSetNumber);

        // 3️⃣ DB에 삽입
        todoMapper.insertTodo(dto);

        // 4️⃣ 응답 DTO 반환
        return TodoCreateResponseDTO.builder()
                .todoId(dto.getTodoId())
                .exerciseId(dto.getExerciseId())
                .setsNumber(dto.getSetsNumber())
                .repsTarget(dto.getRepsTarget())
                .date(dto.getDate())
                .isCompleted(dto.getIsCompleted())
                .message("투두가 성공적으로 생성되었습니다.")
                .build();
    }

    /** 개별 세트 완료 토글 (todoId만으로 true/false 자동 반전) */
    public Map<String, Object> updateTodoCompletion(Long todoId) {
        // 현재 상태 조회
        Boolean currentStatus = todoMapper.getIsCompletedById(todoId);

        // null 방지 + 토글 처리
        Boolean newStatus = (currentStatus != null && currentStatus) ? false : true;

        int updated = todoMapper.updateTodoCompletion(todoId, newStatus);

        Map<String, Object> response = new HashMap<>();
        response.put("todoId", todoId);
        response.put("isCompleted", newStatus);

        if (updated > 0) {
            response.put("message", newStatus
                    ? "세트가 완료 처리되었습니다."
                    : "세트 완료가 해제되었습니다.");
        } else {
            response.put("message", "해당 투두 항목을 찾을 수 없습니다.");
        }

        return response;
    }

    /** ✅ 현재 is_done 상태를 반전시켜 저장 */
    @Transactional
    public boolean toggleTodosDoneStatus(LocalDate date) {
        // 1️⃣ 현재 상태 조회
        boolean currentStatus = todoMapper.existsTodosDoneTrueByDate(date) > 0;

        // 2️⃣ 반전된 상태로 업데이트
        boolean newStatus = !currentStatus;
        todoMapper.updateTodosDoneStatus(date, newStatus);

        return newStatus;
    }

    /** 투두 수정 */
    public Map<String, Object> updateTodo(TodoRequestDTO dto) {
        int updated = todoMapper.updateTodo(dto);

        Map<String, Object> response = new HashMap<>();
        if (updated > 0) {
            response.put("todoId", dto.getTodoId());
            response.put("message", "Todo가 성공적으로 수정되었습니다.");
        } else {
            response.put("message", "해당 Todo가 존재하지 않습니다.");
        }
        return response;
    }

    /** 세트당 수행횟수(reps_target)만 수정 */
    @Transactional
    public void updateTodoRepsOnly(Long todoId, int repsTarget) {
        Map<String, Object> params = new HashMap<>();
        params.put("todoId", todoId);
        params.put("repsTarget", repsTarget);

        todoMapper.updateTodoRepsOnly(params);
    }

    /** 투두 삭제 */
    public Map<String, Object> deleteTodoById(Long todoId) {
        int deleted = todoMapper.deleteTodoById(todoId);
        Map<String, Object> response = new HashMap<>();
        if (deleted > 0) {
            response.put("todoId", todoId);
            response.put("message", "TodoList가 성공적으로 삭제되었습니다.");
        } else {
            response.put("message", "해당 Todo가 존재하지 않습니다.");
        }
        return response;
    }

    /**
     * ✅ 투두 삭제 후 sets_number 재정렬
     */
    @Transactional
    public void deleteTodoAndReorder(Long todoId) {
        // 삭제 대상의 date, exercise_id 조회
        Map<String, Object> info = todoMapper.findDateAndExerciseIdByTodoId(todoId);
        if (info == null || info.isEmpty()) {
            return;
        }

        Object dateObj = info.get("date");
        LocalDate date = (dateObj instanceof LocalDate)
                ? (LocalDate) dateObj
                : ((Date) dateObj).toLocalDate();

        Long exerciseId = ((Number) info.get("exercise_id")).longValue();

        // 1️⃣ 삭제
        todoMapper.deleteTodoById(todoId);

        // 2️⃣ 임시 음수화 (UNIQUE 제약 피하기)
        todoMapper.tempNegateSetsNumbers(date, exerciseId);  // ✅ 수정

        // 3️⃣ 세트번호 재정렬
        todoMapper.reorderSetsNumbers(date, exerciseId);     // ✅ 수정
    }

    /** 휴식 시간 기록 */
    public Map<String, Object> updateRestTime(Long todoId, Integer restTime) {
        // 1️⃣ 현재 로그인한 사용자 정보 가져오기
        String loginId = com.ureca.fitlog.common.SecurityUtil.getLoginId();
        if (loginId == null)
            throw new IllegalStateException("로그인 정보가 없습니다.");

        var user = authMapper.findById(loginId);
        if (user == null)
            throw new IllegalStateException("사용자 정보를 찾을 수 없습니다.");

        Long userId = user.getUserId();

        // 2️⃣ 해당 todo가 본인 소유인지 검증하면서 업데이트
        int updated = todoMapper.updateRestTime(todoId, userId, restTime);

        if (updated == 0) {
            // 다른 사람의 todo이거나 존재하지 않음
            throw new IllegalArgumentException("해당 투두를 찾을 수 없거나 권한이 없습니다.");
        }

        // 3️⃣ 성공 응답
        Map<String, Object> response = new HashMap<>();
        response.put("todoId", todoId);
        response.put("restTime", restTime);
        response.put("message", "휴식시간이 업데이트되었습니다.");

        return response;
    }

    /** ✅ 휴식시간 초기화 */
    public void resetRestTime(Long todoId) {
        // 현재 로그인한 사용자 검증
        String loginId = com.ureca.fitlog.common.SecurityUtil.getLoginId();
        if (loginId == null)
            throw new IllegalStateException("로그인 정보가 없습니다.");

        var user = authMapper.findById(loginId);
        if (user == null)
            throw new IllegalStateException("사용자 정보를 찾을 수 없습니다.");

        Long userId = user.getUserId();

        // 해당 todo가 본인 소유인지 확인하며 초기화
        int updated = todoMapper.resetRestTime(todoId, userId);
        if (updated == 0)
            throw new IllegalArgumentException("해당 투두를 찾을 수 없거나 권한이 없습니다.");
    }

}
