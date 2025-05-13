package com.pado.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "컴포넌트 상세 응답 DTO")
public class ComponentDetailDto {
    @Schema(description = "컴포넌트 정보")
    private ComponentInfo component;
}
