package com.pado.backend.dto.response;

import java.util.List;

import com.pado.backend.global.type.ProjectStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "프로젝트 상세 응답 DTO")
public class ProjectDetailResponseDto {
    @Schema(description = "프로젝트 ID", example = "1")
    private Long projectId;

    @Schema(description = "프로젝트 이름", example = "해커톤 프로젝트")
    private String name;

    @Schema(description = "프로젝트 상태", example = "RUNNING")
    private ProjectStatus status;

    @Schema(description = "프로젝트 설명", example = "이 프로젝트는 해커톤 프로젝트입니다.")
    private String description;

    @Schema(description = "생성일시", example = "2025-05-01T10:30:00")
    private String createdAt;

    @Schema(description = "프로젝트에 속한 컴포넌트 목록")
    private List<ComponentInfo> components;
}

