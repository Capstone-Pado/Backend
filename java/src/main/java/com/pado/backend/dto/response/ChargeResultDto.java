package com.pado.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "과금 비용 계산")
public class ChargeResultDto {

    @Schema(description = "현재 누적 비용", example = "11.5")
    private String currentCost;

    @Schema(description = "사용 시작 시각: ISO 8601", example = "2025-05-01T00:00:00")
    private String usagePeriodStart;

    @Schema(description = "사용 종료 시각: ISO 8601", example = "2025-05-12T14:30:00")
    private String usagePeriodEnd;
}