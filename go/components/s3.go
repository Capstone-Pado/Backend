package components

import (
	"fmt"
	"os"
	"pado/models"
	"pado/utils"
	"path/filepath"
)

func PlanProvisionS3(req models.S3ProvisionRequest) error {
	basePath := fmt.Sprintf("workspaces/%s/%s", req.DeploymentId, req.ComponentId)
	_ = os.MkdirAll(basePath, 0755)

	// 1. 템플릿 렌더링
	tfPath := filepath.Join(basePath, "main.tf")
	err := utils.RenderTemplate("templates/terraform/s3/main.tf.tpl", tfPath, req)
	if err != nil {
		return fmt.Errorf("template render error: %w", err)
	}
	return nil
}

func ProvisionS3(req models.S3ProvisionRequest, logFile *os.File) error {
	basePath := fmt.Sprintf("workspaces/%s/%s", req.DeploymentId, req.ComponentId)

	// 2. Terraform Run
	err := RunTerraform(basePath, logFile)
	if err != nil {
		return fmt.Errorf("terraform run error: %w", err)
	}

	return nil
}

func DestroyS3(deploymentId, componentId string, logFile *os.File) error {
	basePath := fmt.Sprintf("workspaces/%s/%s", deploymentId, componentId)
	if err := runTerraformCmd(basePath, logFile, "destroy", "-auto-approve"); err != nil {
		return fmt.Errorf("terraform destroy error: %w", err)
	}
	return nil
}
