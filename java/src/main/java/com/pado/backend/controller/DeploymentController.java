package com.pado.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.pado.backend.dto.response.ChargeEstimateDto;
import com.pado.backend.dto.response.ChargeResultDto;
import com.pado.backend.dto.response.CheckDto;
import com.pado.backend.dto.response.DefaultResponseDto;
import com.pado.backend.service.DeploymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/projects/{project_id}/deploy")
@RequiredArgsConstructor
@Tag(name = "Deployment", description = "배포 관리 API")
public class DeploymentController {

    private final DeploymentService deploymentService;

    @PostMapping("/start")
    @Operation(summary = "배포 시작")
    public ResponseEntity<DefaultResponseDto> startDeployment(@PathVariable Long projectId) {
        deploymentService.startDeployment(projectId);
        return ResponseEntity.ok(new DefaultResponseDto("배포가 시작되었습니다."));
    }

    @PostMapping("/restart")
    @Operation(summary = "배포 재시작")
    public ResponseEntity<DefaultResponseDto> restartDeployment(@PathVariable Long projectId) {
        deploymentService.restartDeployment(projectId);
        return ResponseEntity.ok(new DefaultResponseDto("배포가 재시작되었습니다."));
    }

    @PostMapping("/stop")
    @Operation(summary = "배포 중지")
    public ResponseEntity<DefaultResponseDto> stopDeployment(@PathVariable Long projectId) {
        deploymentService.stopDeployment(projectId);
        return ResponseEntity.ok(new DefaultResponseDto("배포가 중지되었습니다."));
    }   

    @GetMapping("/status")
    @Operation(summary = "배포 상태 확인")
    public SseEmitter getDeploymentStatus(@PathVariable Long projectId) {
        return deploymentService.getDeploymentStatus(projectId);
    }

    @GetMapping("/check")
    @Operation(summary = "배포 사전 체크")
    public ResponseEntity<CheckDto> checkDeploymentPreconditions(@PathVariable Long projectId) {
        return ResponseEntity.ok(deploymentService.checkDeploymentPreconditions(projectId));
    }

    @GetMapping("/{projectId}/charge")
    @Operation(summary = "예상 요금 조회")
    public ResponseEntity<ChargeEstimateDto> estimateCharge(@PathVariable Long projectId) {
        return ResponseEntity.ok(deploymentService.estimateCharge(projectId));
    }

    @GetMapping("/{projectId}/estimate")
    @Operation(summary = "실제 요금 조회")
    public ResponseEntity<ChargeResultDto> getCharge(@PathVariable Long projectId) {
        return ResponseEntity.ok(deploymentService.getCharge(projectId));
    }


    @GetMapping("/logs")
    @Operation(summary = "배포 로그 스트리밍")
    public SseEmitter streamDeploymentLogs(@PathVariable Long projectId) {
        return deploymentService.streamDeploymentLogs(projectId);
    }
}

