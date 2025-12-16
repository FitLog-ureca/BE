package com.ureca.fitlog.todos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateRestTimeRequestDTO {

    @Schema(description = "휴식시간(초 단위)", example = "60")
    private Integer restTime;
}

