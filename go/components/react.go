package components

import (
	"fmt"
	"os"
	"pado/models"
	"pado/utils"
	"path/filepath"
	"time"
)

func ProvisionReactService(req models.NodeBuildSpecTemplateData) error {
	basePath := "workspaces/" + req.S3.DeploymentID
	info, err := os.Stat(basePath)
	if os.IsNotExist(err) {
		return fmt.Errorf("resource is not exist: %w", err)
	}
	if !info.IsDir() {
		return fmt.Errorf("deploy is not a directory: %w", err)
	}
	basePath = "workspaces/" + req.S3.DeploymentID + "/" + req.Service.ComponentId
	_ = os.MkdirAll(basePath, 0755)

	// 0. 키 쌍 자동 생성
	pubKey, privKey, err := utils.GenerateRSAKeyPair()
	if err != nil {
		return fmt.Errorf("key pair generation error: %w", err)
	}

	os.WriteFile(filepath.Join(basePath, "id_rsa"), privKey, 0600)

	tmp := models.EC2ProvisionRequest{
		AMI:          "ami-0c9c942bd7bf113a2",
		Region:       req.S3.Region,
		ComponentId:  req.Service.ComponentId,
		AWSAccessKey: os.Getenv("AWS_ACCESS_KEY"),
		AWSSecretKey: os.Getenv("AWS_SECRET_KEY"),
	}

	// 0-2. 요청에 키 관련 필드 삽입 (req는 구조체 복사이므로 안전)
	tmp.KeyName = tmp.ComponentId
	tmp.PublicKey = pubKey
	tfPath := filepath.Join(basePath, "main.tf")
	err = utils.RenderTemplate("templates/service/react/main.tf.tpl", tfPath, tmp)
	if err != nil {
		return fmt.Errorf("template render error: %w", err)
	}

	// 2. Terraform init
	if err := runTerraformCmd(basePath, "init"); err != nil {
		return fmt.Errorf("terraform init error: %w", err)
	}

	// 3. Terraform apply
	if err := runTerraformCmd(basePath, "apply", "-auto-approve"); err != nil {
		return fmt.Errorf("terraform apply error: %w", err)
	}

	tplPath := "templates/service/react/init.sh.tpl"
	shPath := filepath.Join(basePath, "init.sh")
	err = utils.RenderTemplate(tplPath, shPath, req)
	if err != nil {
		return fmt.Errorf("template render error: %w", err)
	}

	ip, err := utils.GetResourceIP(req.S3.DeploymentID, req.Service.ComponentId)
	if err != nil {
		return fmt.Errorf("get resource ip error: %w", err)
	}
	err = utils.WaitForSSH(ip, 22, 10*time.Second)
	if err != nil {
		return fmt.Errorf("wait for ssh error: %w", err)
	}
	sshUser := "ubuntu" // 혹은 상황에 맞는 사용자명
	keyPath := fmt.Sprintf("workspaces/%s/%s/id_rsa", req.S3.DeploymentID, req.Service.ComponentId)

	err = utils.RunRemoteScript(ip, sshUser, keyPath, shPath)
	if err != nil {
		return fmt.Errorf("run remote script error: %w", err)
	}
	DestroyEC2(req.S3.DeploymentID, req.Service.ComponentId)
	return nil
}
