package com.pado.backend.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.pado.backend.domain.Credential;
import com.pado.backend.domain.User;
import com.pado.backend.dto.request.CredentialCreateRequestDto;
import com.pado.backend.dto.response.CredentialDetailResponseDto;
import com.pado.backend.dto.response.CredentialResponseDto;
import com.pado.backend.dto.response.DefaultResponseDto;
import com.pado.backend.global.exception.CredentialNotFoundException;
import com.pado.backend.global.exception.UnauthorizedCredentialAccessException;
import com.pado.backend.global.exception.UserNotFoundException;
import com.pado.backend.repository.CredentialRepository;
import com.pado.backend.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
// 서비스 단위 테스트
public class CredentialServiceTest {

    @Mock
    private CredentialRepository credentialRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CredentialService credentialService;

    private User mockUser;
    private Credential mockCredential;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = User.builder().userId(1L).build();
        mockCredential = Credential.builder()
                .credentialId(100L)
                .credentialName("test")
                .credentialType("AWS")
                .credentialDescription("desc")
                .credentialData("data")
                .createdAt(LocalDateTime.now())
                .user(mockUser)
                .build();
    }

    @Test
    void createCredential_success() {
        CredentialCreateRequestDto request = new CredentialCreateRequestDto("test", "desc", "AWS", "data");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(credentialRepository.save(any(Credential.class))).thenReturn(mockCredential);

        CredentialResponseDto response = credentialService.createCredential(request, 1L);

        assertEquals("등록 완료", response.getMessage());
        assertEquals("test", response.getName());
    }

    @Test
    void createCredential_userNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        CredentialCreateRequestDto request = new CredentialCreateRequestDto("test", "desc", "AWS", "data");

        assertThrows(UserNotFoundException.class,
                () -> credentialService.createCredential(request, 999L));
    }

    @Test
    void getAllCredentials_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(credentialRepository.findByUser(mockUser)).thenReturn(List.of(mockCredential));

        List<CredentialResponseDto> result = credentialService.getAllCredentials(1L);

        assertEquals(1, result.size());
        assertEquals("조회 완료", result.get(0).getMessage());
    }

    @Test
    void getCredential_success() {
        when(credentialRepository.findById(100L)).thenReturn(Optional.of(mockCredential));

        CredentialDetailResponseDto detail = credentialService.getCredential(1L, 100L);

        assertEquals("조회 완료", detail.getMessage());
        assertEquals("test", detail.getName());
    }

    @Test
    void getCredential_notFound() {
        when(credentialRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(CredentialNotFoundException.class,
                () -> credentialService.getCredential(1L, 100L));
    }

    @Test
    void getCredential_unauthorizedAccess() {
        User anotherUser = User.builder().userId(2L).build();
        Credential anotherCredential = Credential.builder().credentialId(101L).user(anotherUser).build();

        when(credentialRepository.findById(101L)).thenReturn(Optional.of(anotherCredential));

        assertThrows(UnauthorizedCredentialAccessException.class,
                () -> credentialService.getCredential(1L, 101L));
    }

    @Test
    void deleteCredential_success() {
        when(credentialRepository.findById(100L)).thenReturn(Optional.of(mockCredential));

        DefaultResponseDto result = credentialService.deleteCredential(1L, 100L);

        verify(credentialRepository, times(1)).delete(mockCredential);
        assertEquals("크리덴셜 삭제 완료", result.getMessage());
    }

    @Test
    void deleteCredential_notFound() {
        when(credentialRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(CredentialNotFoundException.class,
                () -> credentialService.deleteCredential(1L, 999L));
    }

    @Test
    void deleteCredential_unauthorizedAccess() {
        User anotherUser = User.builder().userId(2L).build();
        Credential anotherCredential = Credential.builder().credentialId(200L).user(anotherUser).build();

        when(credentialRepository.findById(200L)).thenReturn(Optional.of(anotherCredential));

        assertThrows(UnauthorizedCredentialAccessException.class,
                () -> credentialService.deleteCredential(1L, 200L));
    }
}
