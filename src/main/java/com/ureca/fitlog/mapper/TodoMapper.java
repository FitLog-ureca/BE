package com.ureca.fitlog.mapper;

import com.ureca.fitlog.dto.TodoRequestDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TodoMapper {
    void insertTodo(TodoRequestDTO dto);
}
