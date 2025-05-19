package components

import (
	"fmt"
	"os"
	"pado/models"
	"pado/utils"
	"path/filepath"
	"time"
)

func ProvisionReactService(req models.NodeBuildSpecTemplateData, logFile *os.File) error {
	// 0. Deploy 디렉토리 확인
	basePath := "workspaces/" + req.S3.DeploymentId
	info, err := os.Stat(basePath)
	if os.IsNotExist(err) {
		return fmt.Errorf("resource is not exist: %w", err)
	}
	if !info.IsDir() {
		return fmt.Errorf("deploy is not a directory: %w", err)
	}
	// 0-1. 서비스 디렉토리 생성
	basePath = "workspaces/" + req.S3.DeploymentId + "/" + req.Service.ComponentId
	_ = os.MkdirAll(basePath, 0755)

	// 0-2. 키 쌍 자동 생성
	pubKey, privKey, err := utils.GenerateRSAKeyPair()
	if err != nil {
		return fmt.Errorf("key pair generation error: %w", err)
	}
	os.WriteFile(filepath.Join(basePath, "id_rsa"), privKey, 0600)

	// 1. 임시 EC2 프로비저닝 요청 생성
	tmp := models.EC2ProvisionRequest{
		AMI:          "ami-0c9c942bd7bf113a2",
		Region:       req.S3.Region,
		ComponentId:  req.Service.ComponentId,
		DeploymentId: req.S3.DeploymentId,
		AWSAccessKey: req.S3.AWSAccessKey,
		AWSSecretKey: req.S3.AWSSecretKey,
	}

	tmp.KeyName = tmp.ComponentId
	tmp.PublicKey = pubKey
	tfPath := filepath.Join(basePath, "main.tf")
	err = utils.RenderTemplate("templates/service/react/main.tf.tpl", tfPath, tmp)
	if err != nil {
		return fmt.Errorf("template render error: %w", err)
	}

	// 2. Terraform Run
	err = RunTerraform(basePath, logFile)
	if err != nil {
		return fmt.Errorf("terraform run error: %w", err)
	}

	// 3. 쉘 스크립트 템플릿 렌더링
	tplPath := "templates/service/react/init.sh.tpl"
	shPath := filepath.Join(basePath, "init.sh")
	err = utils.RenderTemplate(tplPath, shPath, req)
	if err != nil {
		return fmt.Errorf("template render error: %w", err)
	}

	// 4. 쉘 스크립트 실행
	ip, err := utils.GetResourceIP(req.S3.DeploymentId, req.Service.ComponentId)
	if err != nil {
		return fmt.Errorf("get resource ip error: %w", err)
	}
	err = utils.WaitForSSH(ip, 22, 10*time.Second)
	if err != nil {
		return fmt.Errorf("wait for ssh error: %w", err)
	}
	sshUser := "ubuntu" // 혹은 상황에 맞는 사용자명
	keyPath := fmt.Sprintf("workspaces/%s/%s/id_rsa", req.S3.DeploymentId, req.Service.ComponentId)

	err = utils.RunRemoteScript(ip, sshUser, keyPath, shPath, logFile)
	if err != nil {
		return fmt.Errorf("run remote script error: %w", err)
	}
	// 5. 임시 EC2 Destroy
	DestroyEC2(req.S3.DeploymentId, req.Service.ComponentId, logFile)
	return nil
}
