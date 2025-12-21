package com.ureca.fitlog.todos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoMonthlySummaryResponseDTO {
    private int year;
    private int month;
    private List<TodoDailySummaryDTO> summaries;
}
