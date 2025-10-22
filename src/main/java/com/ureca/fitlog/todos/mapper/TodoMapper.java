package com.ureca.fitlog.todos.mapper;

import com.ureca.fitlog.todos.dto.TodoRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TodoMapper {
    void insertTodo(TodoRequestDTO dto);
    List<com.ureca.fitlog.todos.dto.TodoResponseDTO.TodoItem> findTodosByDate(@Param("date") LocalDate date);
    int updateTodoCompletion(@Param("todoId") Long todoId, @Param("isCompleted") Boolean isCompleted);
    int updateTodo(com.ureca.fitlog.todos.dto.TodoRequestDTO dto);
    int deleteTodoById(@Param("todoId") Long todoId);
    LocalDateTime findCreatedAtById(Long todoId);
}

