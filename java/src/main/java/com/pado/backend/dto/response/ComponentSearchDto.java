package com.pado.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "컴포넌트 검색 응답 DTO")
public class ComponentSearchDto {
    @Schema(description = "검색된 컴포넌트 템플릿 정보")
    private ComponentTemplate component;
}
