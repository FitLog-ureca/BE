package com.ureca.fitlog.todos.service;

import com.ureca.fitlog.todos.dto.TodoRequestDTO;
import com.ureca.fitlog.todos.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoMapper todoMapper;
    private final com.ureca.fitlog.auth.mapper.AuthMapper authMapper;

    /** 투두 생성 */
    public Map<String, Object> createTodo(TodoRequestDTO dto) {
        todoMapper.insertTodo(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("date", dto.getDate());
        response.put("todoId", dto.getTodoId());
        response.put("isCompleted", false);
        response.put("isDone", false); // ✅ 하루 완료 상태 초기값
        response.put("createdAt", dto.getCreatedAt());
        response.put("message", "TodoList가 성공적으로 생성되었습니다.");
        return response;
    }

    /** ✅ 개별 세트 완료 토글 (todoId만으로 true/false 자동 반전) */
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
