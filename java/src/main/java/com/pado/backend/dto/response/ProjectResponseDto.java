package com.pado.backend.dto.response;

import com.pado.backend.global.type.ProjectStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponseDto {

    @Schema(description = "프로젝트 ID", example = "101")
    private Long projectId;

    @Schema(description = "프로젝트 이름", example = "해커톤 프로젝트")
    private String name;

    @Schema(description = "프로젝트 설명", example = "이 프로젝트는 해커톤 프로젝트 입니다")
    private String description;

    @Schema(description = "프로젝트 상태", example = "RUNNING")
    private ProjectStatus status;

    @Schema(description = "생성 일시", example = "2025-05-12T10:00:00")
    private String createdAt;
}