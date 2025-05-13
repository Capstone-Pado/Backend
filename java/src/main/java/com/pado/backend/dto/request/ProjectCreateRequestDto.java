package com.pado.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateRequestDto {
    @Schema(description = "프로젝트 이름", example = "해커톤 프로젝트")
    private String name;

    @Schema(description = "프로젝트 설명", example = "이 프로젝트는 해커톤 프로젝트입니다.")
    private String description;
}
