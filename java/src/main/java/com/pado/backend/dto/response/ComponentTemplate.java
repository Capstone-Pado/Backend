package com.pado.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "컴포넌트 템플릿 DTO")
public class ComponentTemplate {
    @Schema(description = "컴포넌트 타입", example = "SERVICE")
    private String type;

    @Schema(description = "세부 타입", example = "MySQL")
    private String subtype;

    @Schema(description = "썸네일 이미지 URL", example = "https://cdn.example.com/mysql.png")
    private String thumbnail;
}
