package com.ureca.fitlog.todos.mapper;

import com.ureca.fitlog.todos.dto.TodoRequestDTO;
import com.ureca.fitlog.todos.dto.TodoResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface TodoMapper {

    void insertTodo(TodoRequestDTO dto);
    List<TodoResponseDTO.TodoItem> findTodosByDate(@Param("date") LocalDate date);
    int updateTodoCompletion(@Param("todoId") Long todoId, @Param("isCompleted") Boolean isCompleted);
    int updateTodo(TodoRequestDTO dto);
    int deleteTodoById(@Param("todoId") Long todoId);

    // ✅ is_done 관련 추가
    int updateTodosDoneStatus(@Param("date") LocalDate date, @Param("isDone") Boolean isDone);
    boolean existsTodosDoneTrueByDate(@Param("date") LocalDate date);

    // 참고용
    int countTodosByDate(@Param("date") LocalDate date);
    int countTodosByDateAndNotCompleted(@Param("date") LocalDate date);
}
