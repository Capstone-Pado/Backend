package com.pado.backend.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
// CHECKLIST: 컴포넌트 세부 타입 = 서비스 타입인가?
// [ ]
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "컴포넌트 상세 정보 DTO")
public class ComponentInfo {
    @Schema(description = "컴포넌트 ID", example = "101")
    private Long componentId;

    @Schema(description = "컴포넌트 타입", example = "SERVICE")
    private String type;

    @Schema(description = "컴포넌트 세부 타입", example = "MySQL")
    private String subtype;

    @Schema(description = "썸네일 이미지 URL", example = "https://cdn.example.com/mysql.png")
    private String thumbnail;

    @Schema(description = "상태", example = "RUNNING")
    private String status;

    @Schema(description = "해당 컴포넌트가 소유한 서비스 목록")
    private List<OwnedService> ownedServices;

    @Schema(description = "컴포넌트 간 연결 정보 목록")
    private List<LinkInfo> links;


    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "컴포넌트가 소유한 서비스 정보")
    public static class OwnedService {
        @Schema(description = "서비스 컴포넌트 ID", example = "201")
        private Long serviceComponentId;

        @Schema(description = "서비스 타입", example = "MySQL Read Replica")
        private String serviceType;

        @Schema(description = "서비스 상태", example = "RUNNING")
        private String status;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "컴포넌트 간 연결 정보")
    public static class LinkInfo {
        @Schema(description = "연결된 컴포넌트 ID", example = "202")
        private Long targetComponentId;

        @Schema(description = "연결 방식", example = "HTTP")
        private String linkType;
    }
}
