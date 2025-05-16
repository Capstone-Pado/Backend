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
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;

@Document(collection = "component_status")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ComponentStatusDocument {

    @Id
    private String id;

    // 공통 필드
    private String componentId;
    private String parentComponentId;
    private String deploymentId;
    private ComponentStatus status;
    private LocalDateTime timestamp;

    // EC2 관련
    private String ec2InstanceType;
    private String ec2Region;
    private String ec2Ami;
    private String ec2InstanceName;
    private List<Integer> ec2OpenPorts;

    // Spring 관련
    private String gitRepo;
    private String buildTool;
    private String jdkVersion;
    private String nginxPort;
    private Map<String, String> env;
    private String gitCredentialId;

    // MySQL 관련
    private String mysqlDatabase;
    private String mysqlUser;
    private String mysqlPort;

    // React 관련 → gitCredentialId 재활용
}

// public class ComponentStatusDocument {
//     @Id
//     private String id;

//     private String componentId; // EC2, MySQL, Spring 등 컴포넌트 고유 ID
//     private String deploymentId;
//     private String status; // ex: RUNNING, STOPPED, FAILED
//     private LocalDateTime timestamp;
// }
