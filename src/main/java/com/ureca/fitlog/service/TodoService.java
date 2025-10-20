package com.ureca.fitlog.service;

import com.ureca.fitlog.dto.TodoRequestDTO;
import com.ureca.fitlog.dto.TodoResponseDTO;
import com.ureca.fitlog.mapper.TodoMapper;
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
        response.put("isCompleted", false);
        response.put("message", "TodoList가 성공적으로 생성되었습니다.");
        return response;
    }

    /** 날짜별 투두 조회 */
    public TodoResponseDTO getTodosByDate(LocalDate date) {
        List<TodoResponseDTO.TodoItem> todos = todoMapper.findTodosByDate(date);
        TodoResponseDTO response = new TodoResponseDTO();
        response.setDate(date);
        response.setCalendarStatus(todos.isEmpty() ? "NOT_STARTED" : "IN_PROGRESS");
        response.setTodos(todos);
        response.setMessage("ToDoList가 성공적으로 조회되었습니다.");
        return response;
    }

}
