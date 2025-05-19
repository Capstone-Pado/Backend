package com.pado.backend.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용 가능한 컴포넌트 타입 목록 DTO")
public class ComponentTypeDto {
    @Schema(
        description = "컴포넌트 타입 목록 (예: EC2, S3, Spring 등)",
        example = "[\"EC2\", \"S3\", \"Spring\"]"
    )
    private List<String> types;
}
