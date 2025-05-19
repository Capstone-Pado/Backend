package components

import (
	"fmt"
	"os"
	"pado/models"
	"pado/utils"
	"path/filepath"
)

func ProvisionMySQLService(req models.ServiceRequest, logFile *os.File) error {
	// 0. Init sh 파일 경로 설정
	tplPath := "templates/service/mysql/init.sh.tpl"
	scriptPath := fmt.Sprintf("workspaces/%s/%s", req.DeploymentId, req.ComponentId)
	_ = os.MkdirAll(scriptPath, 0755)

	// 1. 쉘 스크립트 템플릿 렌더링
	shPath := filepath.Join(scriptPath, "init.sh")
	err := utils.RenderTemplate(tplPath, shPath, req)
	if err != nil {
		return fmt.Errorf("template render error: %w", err)
	}

	// 2. 쉘 스크립트 실행
	ip, err := utils.GetResourceIP(req.DeploymentId, req.ParentComponentId)
	if err != nil {
		return fmt.Errorf("get resource ip error: %w", err)
	}
	sshUser := "ubuntu" // 혹은 상황에 맞는 사용자명
	keyPath := fmt.Sprintf("workspaces/%s/%s/id_rsa", req.DeploymentId, req.ParentComponentId)

	err = utils.RunRemoteScript(ip, sshUser, keyPath, shPath, logFile)
	if err != nil {
		return fmt.Errorf("run remote script error: %w", err)
	}

	return nil
}

func DestroyMySQLService(DeploymentId string, ParentComponentId string, ComponentId string, logFile *os.File) error {
	ip, err := utils.GetResourceIP(DeploymentId, ParentComponentId)
	if err != nil {
		return fmt.Errorf("get resource ip error: %w", err)
	}
	sshUser := "ubuntu" // 혹은 상황에 맞는 사용자명
	keyPath := fmt.Sprintf("workspaces/%s/%s/id_rsa", DeploymentId, ParentComponentId)
	command := fmt.Sprintf("sudo docker rm -f %s", ComponentId)
	err = utils.RunRemoteCommand(ip, sshUser, keyPath, command, logFile)
	if err != nil {
		return fmt.Errorf("run remote script error: %w", err)
	}
	return nil
}
