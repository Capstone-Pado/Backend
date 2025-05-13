package com.pado.backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pado.backend.dto.request.ComponentConnectDto;
import com.pado.backend.dto.request.ComponentCreateRequestDto;
import com.pado.backend.dto.request.ComponentSettingDto;
import com.pado.backend.dto.response.ComponentDetailDto;
import com.pado.backend.dto.response.ComponentSearchDto;
import com.pado.backend.dto.response.ComponentServiceUrlDto;
import com.pado.backend.dto.response.ComponentTypeDto;
import com.pado.backend.dto.response.DefaultResponseDto;
import com.pado.backend.repository.ComponentLinkRepository;
import com.pado.backend.repository.ComponentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComponentService {

    private ComponentRepository componentRepository;
    private ComponentLinkRepository componentLinkRepository;
    /*
    컴포넌트 종류 조회
    ComponentTypeDto 클래스 만들기
    */ 
    public List<ComponentTypeDto> getComponentTypes() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getComponentTypes'");
    }
    
    /*
    컴포넌트 검색
    ComponentSummaryDto 만들기
    */ 
    public List<ComponentSearchDto> searchComponents(String keyword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchComponents'");
    }

    // 컴포넌트 배치
    public ComponentDetailDto createComponentToProject(Long projectId, ComponentCreateRequestDto request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchComponents'");
    }

    // 컴포넌트 설정 적용
    // [ ]
    public void applyComponentSetting(Long projectId, Long componentId, ComponentSettingDto request) {
        // TODO Auto-generated method stub
        // Component component = componentRepository.findById(componentId)
        // .orElseThrow(() -> new IllegalArgumentException("해당 컴포넌트를 찾을 수 없습니다."));

        // if (!component.getProject().getId().equals(projectId)) {
        //     throw new IllegalArgumentException("프로젝트와 컴포넌트가 일치하지 않습니다.");
        //     component.setSettings(request.getSettingJson()); // 동적 JSON 설정 저장
        //     component.setUpdatedAt(LocalDateTime.now());

        //     componentRepository.save(component);
        // }
    }

    // 컴포넌트 연결
    public void connectComponent(Long projectId, Long componentId, ComponentConnectDto request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'connectComponent'");
    }

    // 컴포넌트 서비스 접속
    public ComponentServiceUrlDto getComponentServiceUrl(Long projectId, Long componentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getComponentServiceUrl'");
    }

    // 배치된 컴포넌트 검색
    public List<ComponentSearchDto> searchDeployedComponents(Long projectId, String keyword) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchDeployedComponents'");
    }

    // 개별 컴포넌트 상태 조회
    public SseEmitter getComponentStatus(Long projectId, Long componentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getComponentStatus'");
    }

    // 서비스 로그 모니터링
    public SseEmitter streamComponentLogs(Long projectId, Long componentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'streamComponentLogs'");
    }

    // 모니터링
    public SseEmitter streamMonitoring(Long projectId, Long componentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'streamMonitoring'");
    }

    // 컴포넌트 연결 해제
    public DefaultResponseDto disconnectComponent(Long projectId, Long componentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'disconnectComponent'");
    }

    // 컴포넌트 삭제
    public DefaultResponseDto deleteComponent(Long projectId, Long componentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteComponent'");
    }

    
}
