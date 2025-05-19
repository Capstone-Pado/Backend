package com.pado.backend.dto.request;

import java.util.List;

import com.pado.backend.global.type.ComponentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "컴포넌트 생성 요청 DTO")
public class ComponentCreateRequestDto {
    
    @Schema(description = "컴포넌트 정보")
    private ComponentInfo component;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "생성할 컴포넌트 상세 정보")
    public static class ComponentInfo {
        
        // ❌ 새로 생성할 컴포넌트이므로 필요 없음
        // @Schema(description = "컴포넌트 ID", example = "101")
        // private Long componentId;
        
        @Schema(description = "컴포넌트 타입", example = "SERVICE")
        private String type;  // RESOURCE, SERVICE

        @Schema(description = "컴포넌트 세부 타입", example = "MySQL")
        private String subtype; // EC2
        
        @Schema(description = "썸네일 이미지 URL", example = "https://cdn.example.com/mysql.png")
        private String thumbnail;
        
        // CHECKLIST  SERVICE 타입일 때만 필수이며, 이 값을 프론트가 알고 있어야 한다. -> 사용자가 컴포넌트 배치할 때마다 백에서 componentId를 응답 DTO에 포함해주므로 알 수 있음.
        @Schema(description = "부모 컴포넌트 ID (SERVICE일 경우 필수)", example = "101")
        private Long parentComponentId;

        // ❌ SERVICE 목록은 서버에서 조회해서 구성
        // @Schema(description = "해당 컴포넌트가 소유한 서비스 목록")
        // private List<OwnedService> ownedServices;   // EC2 내부의 Spring, MySQL
        
        @Schema(description = "컴포넌트 간 연결 정보 목록")
        private List<LinkInfo> links;
    }

    // @Getter
    // @NoArgsConstructor
    // @AllArgsConstructor
    // @Schema(description = "컴포넌트가 소유한 서비스 정보")
    // public static class OwnedService {
    //     @Schema(description = "서비스 컴포넌트 ID", example = "201")
    //     private Long serviceComponentId;

    //     @Schema(description = "서비스 타입", example = "MySQL Read Replica")
    //     private String serviceType;

    //     @Schema(description = "서비스 상태", example = "DRAFT")
    //     private ComponentStatus status;
    // }

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
