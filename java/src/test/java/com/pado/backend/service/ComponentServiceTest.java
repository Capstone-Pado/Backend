package com.pado.backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pado.backend.domain.Component;
import com.pado.backend.domain.ComponentLink;
import com.pado.backend.domain.Project;
import com.pado.backend.domain.User;
import com.pado.backend.domain.mongo.ComponentSettingDocument;
import com.pado.backend.domain.mongo.ComponentStatusDocument;
import com.pado.backend.dto.request.ComponentCreateRequestDto;
import com.pado.backend.dto.request.ComponentSettingDto;
import com.pado.backend.dto.response.ComponentInfo;
import com.pado.backend.dto.response.ComponentSearchDto;
import com.pado.backend.dto.response.ComponentDetailDto;
import com.pado.backend.dto.response.DefaultResponseDto;
import com.pado.backend.dto.response.ProjectDetailResponseDto;
import com.pado.backend.global.type.ComponentStatus;
import com.pado.backend.global.type.ProjectStatus;
import com.pado.backend.repository.ComponentLinkRepository;
import com.pado.backend.repository.ComponentRepository;
import com.pado.backend.repository.ProjectRepository;
import com.pado.backend.repository.UserRepository;
import com.pado.backend.repository.mongo.ComponentSettingRepository;
import com.pado.backend.repository.mongo.ComponentStatusRepository;

class ComponentServiceTest {

    @InjectMocks
    private ComponentService componentService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ComponentRepository componentRepository;

    @Mock
    private ComponentLinkRepository componentLinkRepository;

    // Mongo
    @Mock
    private ComponentStatusRepository componentStatusRepository;

    @Mock
    private ComponentSettingRepository componentSettingRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createComponentToProject_부모컴포넌트에_두개의_SERVICE가_정상적으로_등록된다() {
        // given
        Long projectId = 1L;
        Long parentComponentId = 99L;

        Project project = Project.builder().projectId(projectId).build();
        Component parent = Component.builder()
            .componentId(parentComponentId)
            .type("RESOURCE")
            .subtype("EC2")
            .project(project)
            .build();

        ComponentCreateRequestDto.ComponentInfo info1 = new ComponentCreateRequestDto.ComponentInfo(
            "SERVICE",
            "MySQL",
            "mysql_thumb.png",
            parentComponentId,
            List.of()
        );

        ComponentCreateRequestDto.ComponentInfo info2 = new ComponentCreateRequestDto.ComponentInfo(
            "SERVICE",
            "Spring",
            "spring_thumb.png",
            parentComponentId,
            List.of()
        );

        ComponentCreateRequestDto request1 = new ComponentCreateRequestDto(info1);
        ComponentCreateRequestDto request2 = new ComponentCreateRequestDto(info2);

        Component savedComponent1 = Component.builder()
            .componentId(10L)
            .type("SERVICE")
            .subtype("MySQL")
            .thumbnail("mysql_thumb.png")
            .project(project)
            .parentComponentId(parent)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        Component savedComponent2 = Component.builder()
            .componentId(11L)
            .type("SERVICE")
            .subtype("Spring")
            .thumbnail("spring_thumb.png")
            .project(project)
            .parentComponentId(parent)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(componentRepository.findById(parentComponentId)).thenReturn(Optional.of(parent));

        // 1차 저장: MySQL
        when(componentRepository.save(any(Component.class))).thenReturn(savedComponent1);
        when(componentRepository.findByParentComponentId(parentComponentId)).thenReturn(List.of(savedComponent1));

        ComponentDetailDto result1 = componentService.createComponentToProject(projectId, request1);

        // 2차 저장: Spring
        when(componentRepository.save(any(Component.class))).thenReturn(savedComponent2);
        when(componentRepository.findByParentComponentId(parentComponentId)).thenReturn(List.of(savedComponent1, savedComponent2));

        ComponentDetailDto result2 = componentService.createComponentToProject(projectId, request2);

        // then
        ComponentInfo parentResponse = result2.getComponent();
        assertThat(parentResponse.getComponentId()).isEqualTo(parentComponentId);
        assertThat(parentResponse.getType()).isEqualTo("RESOURCE");
        assertThat(parentResponse.getOwnedServices()).hasSize(2);
        assertThat(parentResponse.getOwnedServices())
            .extracting("serviceType")
            .containsExactlyInAnyOrder("MySQL", "Spring");

        // 상태 저장 검증 (DRAFT)
        verify(componentStatusRepository, times(2)).save(any(ComponentStatusDocument.class));

        ArgumentCaptor<ComponentStatusDocument> statusCaptor = ArgumentCaptor.forClass(ComponentStatusDocument.class);
        verify(componentStatusRepository, atLeastOnce()).save(statusCaptor.capture());

        List<ComponentStatusDocument> savedStatuses = statusCaptor.getAllValues();
        assertThat(savedStatuses).hasSize(2); // 두 개의 SERVICE 컴포넌트

        for (ComponentStatusDocument status : savedStatuses) {
            assertThat(status.getStatus()).isEqualTo(ComponentStatus.DRAFT);
            assertThat(status.getComponentId()).isNotNull();
        }
    }

    @Test
    void searchDeployedComponents_keyword_null_전체조회() {
        // given
        Long projectId = 1L;
        Project project = Project.builder().projectId(projectId).build();

        List<Component> components = List.of(
            Component.builder().type("RESOURCE").subtype("EC2").thumbnail("ec2.png").project(project).build(),
            Component.builder().type("SERVICE").subtype("MySQL").thumbnail("mysql.png").project(project).build()
        );

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(componentRepository.findByProject(project)).thenReturn(components);

        // when
        List<ComponentSearchDto> result = componentService.searchDeployedComponents(projectId, null);

        // then
        assertThat(result).hasSize(2);
        assertThat(result)
            .extracting(dto -> dto.getComponent().getSubtype())
            .containsExactlyInAnyOrder("EC2", "MySQL");
    }

    @Test
    void searchDeployedComponents_keyword_blank_빈리스트반환() {
        // given
        Long projectId = 1L;
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(Project.builder().projectId(projectId).build()));

        // when
        List<ComponentSearchDto> result = componentService.searchDeployedComponents(projectId, "   ");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void searchDeployedComponents_keyword_입력시_검색수행() {
        // given
        Long projectId = 1L;
        String keyword = "Spring";
        Project project = Project.builder().projectId(projectId).build();

        List<Component> searchResult = List.of(
            Component.builder().type("SERVICE").subtype("Spring").thumbnail("spring.png").project(project).build()
        );

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(componentRepository.searchComponentsByProjectAndKeyword(projectId, keyword)).thenReturn(searchResult);

        // when
        List<ComponentSearchDto> result = componentService.searchDeployedComponents(projectId, keyword);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getComponent().getSubtype()).isEqualTo("Spring");
    }


    
    @Test
    void applyComponentSetting_정상요청시_설정과상태가_저장된다() {
        // given
        Long projectId = 1L;
        Long componentId = 100L;
        String deploymentId = "deployment-001";
        String settingJson = """
            {
              "DeploymentId": "deployment-001",
              "ComponentId": "spring-001",
              "Spring": {
                "GitRepo": "https://github.com/example/spring-app.git"
              }
            }
            """;

        Project project = Project.builder().projectId(projectId).build();
        Component component = Component.builder()
            .componentId(componentId)
            .project(project)
            .build();

        ComponentSettingDto request = new ComponentSettingDto(settingJson);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        when(componentRepository.findById(componentId)).thenReturn(Optional.of(component));

        // when
        DefaultResponseDto response = componentService.applyComponentSetting(projectId, componentId, request);

        // then
        verify(componentSettingRepository, times(1)).save(any(ComponentSettingDocument.class));
        verify(componentStatusRepository, times(1)).save(any(ComponentStatusDocument.class));

        ArgumentCaptor<ComponentStatusDocument> statusCaptor = ArgumentCaptor.forClass(ComponentStatusDocument.class);
        verify(componentStatusRepository).save(statusCaptor.capture());

        ComponentStatusDocument savedStatus = statusCaptor.getValue();
        assertThat(savedStatus.getStatus()).isEqualTo(ComponentStatus.START);
        assertThat(savedStatus.getDeploymentId()).isEqualTo("deployment-001");

        assertThat(response.getMessage()).isEqualTo("설정 적용 완료");
    }

    // 컴포넌트 삭제
    @Test
    void deleteComponent_링크포함_정상삭제된다() {
        // given
        Long projectId = 1L;
        Long componentId = 10L;

        Project project = Project.builder().projectId(projectId).build();

        ComponentLink dummyLink = ComponentLink.builder()
            .connectionType(ComponentLink.ConnectionType.HTTP)
            .createdAt(LocalDateTime.now())
            .build();

        Component component = Component.builder()
            .componentId(componentId)
            .project(project)
            .fromLinks(List.of(dummyLink))
            .toLinks(List.of(dummyLink))
            .build();

        ComponentStatusDocument dummyStatus = ComponentStatusDocument.builder()
            .componentId(String.valueOf(componentId))
            .status(ComponentStatus.DRAFT)
            .timestamp(LocalDateTime.now())
            .build();

        when(componentRepository.findById(componentId)).thenReturn(Optional.of(component));
        when(componentStatusRepository.findTopByComponentIdOrderByTimestampDesc(componentId.toString()))
            .thenReturn(Optional.of(dummyStatus));

        // when
        DefaultResponseDto result = componentService.deleteComponent(projectId, componentId);

        // then
        verify(componentLinkRepository, times(2)).deleteAll(anyList());
        verify(componentRepository, times(1)).delete(component);
        assertThat(result.getMessage()).isEqualTo("컴포넌트 삭제 완료");
    }

}
