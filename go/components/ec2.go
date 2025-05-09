package components

import (
	"fmt"
	"os"
	"os/exec"
	"pado/models"
	"pado/utils"
	"path/filepath"
)

func ProvisionEC2(req models.EC2ProvisionRequest) error {
	basePath := fmt.Sprintf("workspaces/%s/%s", req.DeploymentID, req.ComponentId)
	_ = os.MkdirAll(basePath, 0755)

	// 0. 키 쌍 자동 생성
	pubKey, privKey, err := utils.GenerateRSAKeyPair()
	if err != nil {
		return fmt.Errorf("key pair generation error: %w", err)
	}

	os.WriteFile(filepath.Join(basePath, "id_rsa"), privKey, 0600)
	// 0-2. 요청에 키 관련 필드 삽입 (req는 구조체 복사이므로 안전)
	req.KeyName = req.ComponentId
	req.PublicKey = pubKey

	// 1. 템플릿 렌더링
	tfPath := filepath.Join(basePath, "main.tf")
	err = utils.RenderTemplate("templates/terraform/ec2/main.tf.tpl", tfPath, req)
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

	return nil
}

func DestroyEC2(deploymentID, componentID string) error {
	basePath := fmt.Sprintf("workspaces/%s/%s", deploymentID, componentID)
	if err := runTerraformCmd(basePath, "destroy", "-auto-approve"); err != nil {
		return fmt.Errorf("terraform destroy error: %w", err)
	}
	return nil
}

func runTerraformCmd(dir string, args ...string) error {
	cmd := exec.Command("terraform", args...)
	cmd.Dir = dir
	cmd.Stdout = os.Stdout
	cmd.Stderr = os.Stderr
	return cmd.Run()
}
