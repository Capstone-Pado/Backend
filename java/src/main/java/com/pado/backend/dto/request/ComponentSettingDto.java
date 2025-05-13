package com.pado.backend.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ComponentSettingDto {
    @Schema(description = "컴포넌트 설정 정보 (JSON 형태 문자열)", example = "{\"key\": \"value\"}")
    private String settingJson;
}
