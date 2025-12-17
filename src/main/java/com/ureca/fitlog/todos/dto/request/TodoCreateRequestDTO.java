package com.ureca.fitlog.todos.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "운동 항목 생성 요청")
public class TodoCreateRequestDTO {
    @Schema(hidden = true)
    private Long todoId;

    @Schema(hidden = true)
    private Long userId;

    @Schema(description = "운동 날짜", example = "2025-01-01")
    private LocalDate date;       // 운동 날짜

    @Schema(description = "운동 종목 ID", example = "3")
    private Long exerciseId;      // 운동 종목 ID

    @Schema(hidden = true)
    private Integer setsNumber;   // 세트 수

    @Schema(description = "목표 횟수 (선택)", example = "10", nullable = true)
    private Integer repsTarget;   // 세트당 목표 횟수

    @Schema(description = "중량 (선택)", example = "60.0", nullable = true)
    private Double weight;        // 중량, (유산소면 null)

    @Schema(hidden = true)
    private Integer restTime;     // 총 휴식시간 (초 단위)
}
