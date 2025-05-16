package com.pado.backend.domain;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Component {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long componentId;

    private String componentName;
    private String type;
    private String subtype;
    private String thumbnail;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // N:1 관계 - 하나의 프로젝트에 여러 컴포넌트
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    // 자기 참조 관계 (부모 컴포넌트)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_component_id")
    private Component parentComponentId;

    // 자기 참조 관계 (자식 컴포넌트들)
    @OneToMany(mappedBy = "parentComponentId", cascade = CascadeType.ALL)
    private List<Component> childComponents;

    // 연결 정보 - from에서 연결된 링크들
    @OneToMany(mappedBy = "fromComponentId", cascade = CascadeType.ALL)
    private List<ComponentLink> fromLinks;

    // 연결 정보 - to로 연결된 링크들
    @OneToMany(mappedBy = "toComponentId", cascade = CascadeType.ALL)
    private List<ComponentLink> toLinks;

}
