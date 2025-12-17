package com.ureca.fitlog.todos.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoCreateRequestDTO {

    private LocalDate date;     // 운동 날짜
    private Long exerciseId;    // 운동 종목 ID
}
