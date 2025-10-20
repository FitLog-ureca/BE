package com.ureca.fitlog.service;

import com.ureca.fitlog.dto.TodoRequestDTO;
import com.ureca.fitlog.mapper.TodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoMapper todoMapper;

    public Map<String, Object> createTodo(TodoRequestDTO dto) {
        todoMapper.insertTodo(dto);

        Map<String, Object> response = new HashMap<>();
        response.put("date", dto.getDate());
        response.put("isCompleted", false);
        response.put("message", "TodoList가 성공적으로 생성되었습니다.");
        return response;
    }
}
