package com.ureca.fitlog.todos.service;

import com.ureca.fitlog.todos.dto.TodoRequestDTO;
import com.ureca.fitlog.todos.dto.TodoResponseDTO;
import com.ureca.fitlog.todos.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoMapper todoMapper;

    /** 투두 생성 */
    public Map<String, Object> createTodo(TodoRequestDTO dto) {
        todoMapper.insertTodo(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("date", dto.getDate());
        response.put("todoId", dto.getTodoId());
        response.put("isCompleted", false);
        response.put("isDone", false); // ✅ 하루 완료 상태 초기값
        response.put("message", "TodoList가 성공적으로 생성되었습니다.");
        return response;
    }

    /** 날짜별 투두 조회 */
    public TodoResponseDTO getTodosByDate(LocalDate date) {
        List<TodoResponseDTO.TodoItem> todos = todoMapper.findTodosByDate(date);
        TodoResponseDTO response = new TodoResponseDTO();
        response.setDate(date);
        response.setTodos(todos);
        response.setMessage(todos.isEmpty()
                ? "등록된 Todo가 없습니다."
                : "TodoList가 성공적으로 조회되었습니다.");
        return response;
    }

    /** ✅ 개별 세트 완료 (is_completed true/false 명시적 반영) */
    public Map<String, Object> updateTodoCompletion(Long todoId, Boolean isCompleted) {
        int updated = todoMapper.updateTodoCompletion(todoId, isCompleted);

        Map<String, Object> response = new HashMap<>();
        response.put("todoId", todoId);
        response.put("isCompleted", isCompleted);

        if (updated > 0) {
            response.put("message", isCompleted
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
        int updated = todoMapper.updateRestTime(todoId, restTime);

        Map<String, Object> response = new HashMap<>();
        response.put("todoId", todoId);
        response.put("restTime", restTime);
        response.put("message", updated > 0
                ? "세트의 휴식시간이 기록되었습니다."
                : "해당 투두 항목을 찾을 수 없습니다.");

        return response;
    }

}
