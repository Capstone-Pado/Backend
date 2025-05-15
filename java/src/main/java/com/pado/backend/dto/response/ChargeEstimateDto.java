package com.pado.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "예상 비용 계산")
public class ChargeEstimateDto {

    @Schema(description = "측정된 예상 비용", example = "11.5 달러/월")
    private String estimatedCost;

    @Schema(description = "예상 비용 계산이 수행된 기준 시각: ISO 8601", example = "2025-05-12T14:30:00")
    private String calculationTime;
}
