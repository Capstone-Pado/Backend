package com.pado.backend.domain.mongo;

import org.springframework.data.mongodb.core.mapping.Document;

import com.pado.backend.global.type.ComponentStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

@Document(collection = "component_status")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
// TODO : 컴포넌트 상태 변경시에 계속 몽고디비에 저장시켜 놔야한다. 그래야 최신 상태를 갖다가 쓰지
public class ComponentStatusDocument {

    @Id
    private String id;

    // 공통 필드
    private String componentId; // CHECKLIST Go에게 전달할 때 사용하는 ID라서 타입이 String
    private String parentComponentId;
    private String deploymentId;
    private ComponentStatus status;
    private LocalDateTime timestamp;
}
