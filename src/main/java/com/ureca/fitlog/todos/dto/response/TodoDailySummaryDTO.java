package com.ureca.fitlog.todos.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("isDone")
    private boolean isDone;

    @JsonIgnore
    public boolean isDone() {
        return isDone;
    }
}