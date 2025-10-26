package com.ureca.fitlog.todos.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateRestResponseDTO {
    private Long todoId;
    private Integer restTime;
    private String message;
}
