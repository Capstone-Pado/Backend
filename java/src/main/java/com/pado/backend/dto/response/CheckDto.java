package com.pado.backend.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "배포 사전 체크")
public class CheckDto {

    @Schema(description = "배포 준비 여부", example = "true")
    private boolean ready;

    @Schema(description = "배포 전 유의사항", example = "[\"과금 발생 주의\", \"무료 티어 한도 주의\", \"리소스 정리 필요\", \"책임 안내\"]")
    private List<String> warnings;

    @Schema(description = "결과 메시지", example = "배포 시작")
    private String message;
}
