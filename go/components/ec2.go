package components

import (
	"fmt"
	"os"
	"pado/models"
	"pado/utils"
	"path/filepath"
	"time"
)

func PlanProvisionEC2(req models.EC2ProvisionRequest) error {
	basePath := fmt.Sprintf("workspaces/%s/%s", req.DeploymentId, req.ComponentId)
	_ = os.MkdirAll(basePath, 0755)

	// 1. SSH 키 생성
	pubKey, privKey, err := utils.GenerateRSAKeyPair()
	if err != nil {
		return fmt.Errorf("key pair generation error: %w", err)
	}

	os.WriteFile(filepath.Join(basePath, "id_rsa"), privKey, 0600)

	req.KeyName = req.ComponentId
	req.PublicKey = pubKey

	// 2. Terraform Template 렌더링
	tfPath := filepath.Join(basePath, "main.tf")
	err = utils.RenderTemplate("templates/terraform/ec2/main.tf.tpl", tfPath, req)
	if err != nil {
		return fmt.Errorf("template render error: %w", err)
	}
	return nil
}

func ProvisionEC2(req models.EC2ProvisionRequest, logFile *os.File) error {
	basePath := fmt.Sprintf("workspaces/%s/%s", req.DeploymentId, req.ComponentId)

	// 3. Terraform Run
	err := RunTerraform(basePath, logFile)
	if err != nil {
		return fmt.Errorf("terraform run error: %w", err)
	}
	ip, err := utils.GetResourceIP(req.DeploymentId, req.ComponentId)
	if err != nil {
		return fmt.Errorf("get resource ip error: %w", err)
	}
	// 4. SSH 접속 대기
	utils.WaitForSSH(ip, 22, 60*time.Second)
	return nil
}

func DestroyEC2(deploymentId, componentId string, logFile *os.File) error {
	basePath := fmt.Sprintf("workspaces/%s/%s", deploymentId, componentId)
	if err := runTerraformCmd(basePath, logFile, "destroy", "-auto-approve"); err != nil {
		return fmt.Errorf("terraform destroy error: %w", err)
	}
	return nil
}
