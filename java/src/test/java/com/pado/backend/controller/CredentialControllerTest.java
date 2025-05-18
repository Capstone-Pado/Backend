package com.pado.backend.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pado.backend.dto.request.CredentialCreateRequestDto;
import com.pado.backend.dto.response.CredentialDetailResponseDto;
import com.pado.backend.dto.response.CredentialResponseDto;
import com.pado.backend.dto.response.DefaultResponseDto;
import com.pado.backend.service.CredentialService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

@WebMvcTest(CredentialController.class)
class CredentialControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CredentialService credentialService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /user/{userId}/credential - 크레덴셜 생성")
    void createCredential() throws Exception {
        Long userId = 1L;
        CredentialCreateRequestDto requestDto = new CredentialCreateRequestDto("test", "desc", "AWS", "data");

        CredentialResponseDto responseDto = new CredentialResponseDto(
                1L, "test", "AWS", "desc", "등록 완료", "2025-05-13T10:00:00"
        );

        when(credentialService.createCredential(any(), eq(userId))).thenReturn(responseDto);

        mockMvc.perform(post("/user/{userId}/credential", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credentialId").value(1))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.type").value("AWS"))
                .andExpect(jsonPath("$.message").value("등록 완료"));
    }

    @Test
    @DisplayName("GET /user/{userId}/credential - 크레덴셜 전체 조회")
    void getAllCredentials() throws Exception {
        Long userId = 1L;

        List<CredentialResponseDto> list = List.of(new CredentialResponseDto(
                1L, "test", "AWS", "desc", "조회 완료", "2025-05-13T10:00:00"
        ));

        when(credentialService.getAllCredentials(userId)).thenReturn(list);

        mockMvc.perform(get("/user/{userId}/credential", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].credentialId").value(1))
                .andExpect(jsonPath("$[0].type").value("AWS"));
    }

    @Test
    @DisplayName("GET /user/{userId}/credential/{credentialId} - 단일 크레덴셜 조회")
    void getCredential() throws Exception {
        Long userId = 1L;
        Long credentialId = 10L;

        CredentialDetailResponseDto detailDto = new CredentialDetailResponseDto(
                10L, "test", "AWS", "desc", "data", "조회 완료", "2025-05-13T10:00:00"
        );

        when(credentialService.getCredential(userId, credentialId)).thenReturn(detailDto);

        mockMvc.perform(get("/user/{userId}/credential/{credentialId}", userId, credentialId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.credentialId").value(10))
                .andExpect(jsonPath("$.name").value("test"));
    }

    @Test
    @DisplayName("DELETE /user/{userId}/credential/{credentialId} - 삭제")
    void deleteCredential() throws Exception {
        Long userId = 1L;
        Long credentialId = 10L;

        when(credentialService.deleteCredential(userId, credentialId))
                .thenReturn(new DefaultResponseDto("크리덴셜 삭제 완료"));

        mockMvc.perform(delete("/user/{userId}/credential/{credentialId}", userId, credentialId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("크리덴셜 삭제 완료"));
    }
}
