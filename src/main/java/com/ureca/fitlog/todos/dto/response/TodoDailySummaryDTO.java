package com.ureca.fitlog.todos.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoDailySummaryDTO {
    private LocalDate date;
    private int totalSets;
    private int completedSets;
}
