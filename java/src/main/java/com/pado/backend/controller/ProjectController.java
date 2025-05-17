package com.pado.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pado.backend.dto.request.ProjectCreateRequestDto;
import com.pado.backend.dto.response.DefaultResponseDto;
import com.pado.backend.dto.response.ProjectDetailResponseDto;
import com.pado.backend.dto.response.ProjectResponseDto;
import com.pado.backend.service.ProjectService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
// [x] : EndPoint 변경
@RequestMapping("user/{userId}/projects")
@Tag(name = "Project", description = "프로젝트 생성/조회/삭제 API")
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    @Operation(summary = "프로젝트 생성")
    public ResponseEntity<ProjectResponseDto> createProject(
            @RequestBody ProjectCreateRequestDto request,
            @PathVariable Long userId) {
        return ResponseEntity.ok(projectService.createProject(request, userId));
    }

    @GetMapping
    @Operation(summary = "프로젝트 전체 조회")
    public ResponseEntity<List<ProjectResponseDto>> getAllProjects(
            @PathVariable Long userId ) {
        return ResponseEntity.ok(projectService.getAllProjects(userId));
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "프로젝트 개별 조회")
    public ResponseEntity<ProjectDetailResponseDto> getProjectById(
        @PathVariable Long userId,    
        @PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProjectById(userId, projectId));
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "프로젝트 개별 삭제")
    public ResponseEntity<DefaultResponseDto> deleteProject(
        @PathVariable Long userId,    
        @PathVariable Long projectId) {
        projectService.deleteProject(userId, projectId);
        return ResponseEntity.ok(new DefaultResponseDto("삭제 완료"));
    }
} 
