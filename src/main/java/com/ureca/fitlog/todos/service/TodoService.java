package com.ureca.fitlog.todos.service;

import com.ureca.fitlog.todos.dto.TodoRequestDTO;
import com.ureca.fitlog.todos.dto.TodoResponseDTO;
import com.ureca.fitlog.todos.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        LocalDateTime createdAt = todoMapper.findCreatedAtById(dto.getTodoId());
        Map<String, Object> response = new HashMap<>();
        response.put("date", dto.getDate());
        response.put("todoId", dto.getTodoId());
        response.put("createdAt", createdAt);
        response.put("isCompleted", false);
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
    /** 투두 완료 체크 */
    public Map<String, Object> updateTodoCompletion(Long todoId, Boolean isCompleted) {
        int updated = todoMapper.updateTodoCompletion(todoId, isCompleted);
        Map<String, Object> response = new HashMap<>();
        if (updated > 0) {
            response.put("todoId", todoId);
            response.put("isCompleted", isCompleted);
            response.put("message", "TodoList가 성공적으로 갱신되었습니다.");
        } else {
            response.put("message", "해당 Todo가 존재하지 않습니다.");
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
}
