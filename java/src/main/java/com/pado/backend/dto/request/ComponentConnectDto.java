package com.pado.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ComponentConnectDto {
    @Schema(description = "현재 컴포넌트가 연결할 대상 컴포넌트 ID", example = "202")
    private Long targetComponentId;

    @Schema(description = "연결 타입", example = "HTTP")
    private String connectionType;
}
