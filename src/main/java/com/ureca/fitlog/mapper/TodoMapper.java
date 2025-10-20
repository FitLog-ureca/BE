package com.ureca.fitlog.mapper;

import com.ureca.fitlog.dto.TodoRequestDTO;
import com.ureca.fitlog.dto.TodoResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TodoMapper {
    void insertTodo(TodoRequestDTO dto);
    List<TodoResponseDTO.TodoItem> findTodosByDate(@Param("date") LocalDate date);
    int updateTodoCompletion(@Param("todoId") Long todoId, @Param("isCompleted") Boolean isCompleted);
    int deleteTodoById(@Param("todoId") Long todoId);
}

