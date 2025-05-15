package com.pado.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pado.backend.dto.request.ProjectCreateRequestDto;
import com.pado.backend.dto.response.DefaultResponseDto;
import com.pado.backend.dto.response.ProjectDetailResponseDto;
import com.pado.backend.dto.response.ProjectResponseDto;
import com.pado.backend.repository.ComponentLinkRepository;
import com.pado.backend.repository.ComponentRepository;
import com.pado.backend.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;
// TODO
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ComponentRepository componentRepository;
    private final ComponentLinkRepository componentLinkRepository;

    public ProjectResponseDto createProject(ProjectCreateRequestDto request) {
        // 구현 생략
        return null;
    }

    public List<ProjectResponseDto> getAllProjects() {
        // 구현 생략
        return null;
    }

    public ProjectDetailResponseDto getProjectById(Long projectId) {
        // 구현 생략
        return null;
    }

    public DefaultResponseDto deleteProject(Long projectId) {
        // 구현 생략
        return null;
    }
}
