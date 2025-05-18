package com.pado.backend.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "컴포넌트 서비스 URL 응답 DTO")
public class ComponentServiceUrlDto {
    @Schema(description = "서비스 접근 URL", example = "http://spring-app.local")
    private String serviceUrl;

    @Schema(description = "컴포넌트 상태", example = "RUNNING")
    private String status;
}

