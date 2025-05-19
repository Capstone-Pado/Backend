package com.pado.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.transaction.annotation.Transactional;

import com.pado.backend.dto.response.ChargeEstimateDto;
import com.pado.backend.dto.response.ChargeResultDto;
import com.pado.backend.dto.response.CheckDto;
import com.pado.backend.repository.ComponentLinkRepository;
import com.pado.backend.repository.ComponentRepository;
import com.pado.backend.repository.DeploymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DeploymentService {

    private final DeploymentRepository deploymentRepository;
    private final ComponentRepository componentRepository;
    private final ComponentLinkRepository componentLinkRepository;

    public void startDeployment(Long projectId) {
        // TODO: 실제 배포 시작 로직 구현
    }

    public void restartDeployment(Long projectId) {
        // TODO: 실제 배포 재시작 로직 구현
    }

    public void stopDeployment(Long projectId) {
        // TODO: 실제 배포 중지 로직 구현
    }

    public CheckDto checkDeploymentPreconditions(Long projectId) {
        // TODO: 사전 체크 로직 구현
        return null;
    }

    public ChargeEstimateDto estimateCharge(Long projectId) {
        // TODO: 예상 요금 계산 로직 구현
        return null;
    }

    public ChargeResultDto getCharge(Long projectId) {
        // TODO: 실제 요금 조회 로직 구현
        return null;
    }

    public SseEmitter getDeploymentStatus(Long projectId) {
        // TODO: SSE로 상태 업데이트 전송
        SseEmitter emitter = new SseEmitter();
        // 예시: emitter.send("STATUS: RUNNING");
        return emitter;
    }

    public SseEmitter streamDeploymentLogs(Long projectId) {
        // TODO: SSE로 로그 스트리밍 구현
        SseEmitter emitter = new SseEmitter();
        // 예시: emitter.send("[INFO] 서비스 시작 중...");
        return emitter;
    }
}

