package com.pado.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pado.backend.domain.User;
import com.pado.backend.dto.request.ProjectCreateRequestDto;
import com.pado.backend.repository.UserRepository;
import com.pado.backend.repository.ProjectRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ProjectIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;

    private Long userId;

    @BeforeEach
    void setup() {
        projectRepository.deleteAll();
        userRepository.deleteAll();

        User user = userRepository.save(User.builder()
                .email("test@example.com")
                .userName("tester")
                .password("hashed123")
                .build());

        userId = user.getUserId();
    }

    @Test
    @DisplayName("프로젝트 생성 후 단건 조회까지 성공한다")
    void createAndFetchProject() throws Exception {
        // given
        ProjectCreateRequestDto request = new ProjectCreateRequestDto("통합 테스트 프로젝트", "테스트 설명");
        String requestBody = objectMapper.writeValueAsString(request);

        // when: 생성
        String response = mockMvc.perform(post("/user/{userId}/projects", userId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("통합 테스트 프로젝트"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long projectId = objectMapper.readTree(response).get("projectId").asLong();

        // then: 단건 조회
        mockMvc.perform(get("/user/{userId}/projects/{projectId}", userId, projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.projectId").value(projectId))
                .andExpect(jsonPath("$.name").value("통합 테스트 프로젝트"));
    }
}
