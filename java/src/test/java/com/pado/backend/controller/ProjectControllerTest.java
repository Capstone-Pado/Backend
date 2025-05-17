package com.pado.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pado.backend.dto.request.ProjectCreateRequestDto;
import com.pado.backend.dto.response.*;
import com.pado.backend.global.type.ProjectStatus;
import com.pado.backend.service.ProjectService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private ProjectService projectService;

    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("프로젝트 생성 API 테스트")
    void createProject_success() throws Exception {
        // given
        Long userId = 1L;
        ProjectCreateRequestDto request = new ProjectCreateRequestDto("Test", "설명");
        ProjectResponseDto response = new ProjectResponseDto(100L, "Test", "설명", ProjectStatus.DRAFT, LocalDateTime.now().toString());

        Mockito.when(projectService.createProject(any(), eq(userId))).thenReturn(response);

        // when & then
        mockMvc.perform(post("/user/{userId}/projects", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectId").value(100L))
            .andExpect(jsonPath("$.status").value(ProjectStatus.DRAFT.getCode()));
    }

    @Test
    @DisplayName("프로젝트 전체 조회 API 테스트")
    void getAllProjects_success() throws Exception {
        Long userId = 1L;

        List<ProjectResponseDto> list = List.of(
                new ProjectResponseDto(101L, "P1", "설명", ProjectStatus.DRAFT, LocalDateTime.now().toString())
        );

        Mockito.when(projectService.getAllProjects(userId)).thenReturn(list);

        mockMvc.perform(get("/user/{userId}/projects", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].projectId").value(101L));
    }

    @Test
    @DisplayName("프로젝트 단건 조회 API 테스트")
    void getProjectById_success() throws Exception {
        Long userId = 1L;
        Long projectId = 200L;

        ProjectDetailResponseDto dto = new ProjectDetailResponseDto(
                projectId, "P2", ProjectStatus.START, "설명", LocalDateTime.now().toString(), List.of());

        Mockito.when(projectService.getProjectById(userId, projectId)).thenReturn(dto);

        mockMvc.perform(get("/user/{userId}/projects/{projectId}", userId, projectId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.projectId").value(projectId));
    }

    @Test
    @DisplayName("프로젝트 삭제 API 테스트")
    void deleteProject_success() throws Exception {
        Long userId = 1L;
        Long projectId = 300L;

        when(projectService.deleteProject(userId, projectId))
            .thenReturn(new DefaultResponseDto("삭제 완료"));

        mockMvc.perform(delete("/user/{userId}/projects/{projectId}", userId, projectId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("삭제 완료"));
    }
}
