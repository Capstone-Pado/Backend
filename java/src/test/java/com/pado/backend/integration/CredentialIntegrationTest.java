package com.pado.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pado.backend.domain.User;
import com.pado.backend.dto.request.CredentialCreateRequestDto;
import com.pado.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CredentialIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long userId;

    @BeforeEach
    void setUp() {
        User user = userRepository.save(User.builder()
            .userName("tester")
            .email("test@example.com")
            .password("encoded-pw")
            .build());

        userId = user.getUserId();
    }

    @Test
    @DisplayName("통합 테스트: Credential 등록 → 조회 → 삭제")
    void createGetAndDeleteCredential() throws Exception {
        // 1. 등록
        CredentialCreateRequestDto request = new CredentialCreateRequestDto(
                "My AWS Credential",
                "IAM 키 설명",
                "AWS",
                "AKIAIOSFODNN7EXAMPLE/secret"
        );

        String jsonRequest = objectMapper.writeValueAsString(request);

        String responseJson = mockMvc.perform(post("/user/{userId}/credential", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("My AWS Credential"))
                .andExpect(jsonPath("$.type").value("AWS"))
                .andExpect(jsonPath("$.message").value("등록 완료"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 2. credentialId 파싱
        Long credentialId = objectMapper.readTree(responseJson).get("credentialId").asLong();

        // 3. 전체 조회
        mockMvc.perform(get("/user/{userId}/credential", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].credentialId").value(credentialId));

        // 4. 삭제
        mockMvc.perform(delete("/user/{userId}/credential/{credentialId}", userId, credentialId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("크리덴셜 삭제 완료"));

        // 5. 단일 조회 시 404 or 예외 발생 확인
        mockMvc.perform(get("/user/{userId}/credential/{credentialId}", userId, credentialId))
                .andExpect(status().is4xxClientError()); // 보통 404 (커스텀 예외에 따라 다름)
    }
}