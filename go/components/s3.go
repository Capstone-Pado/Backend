package components

import (
	"fmt"
	"os"
	"pado/models"
	"pado/utils"
	"path/filepath"
)

func ProvisionS3(req models.S3ProvisionRequest) error {
	basePath := fmt.Sprintf("workspaces/%s/%s", req.DeploymentID, req.ComponentId)
	_ = os.MkdirAll(basePath, 0755)

	// 1. 템플릿 렌더링
	tfPath := filepath.Join(basePath, "main.tf")
	err := utils.RenderTemplate("templates/terraform/s3/main.tf.tpl", tfPath, req)
	if err != nil {
		return fmt.Errorf("template render error: %w", err)
	}

	// 2. Terraform Run
	err = RunTerraform(basePath)
	if err != nil {
		return fmt.Errorf("terraform run error: %w", err)
	}

	return nil
}

func DestroyS3(deploymentID, componentID string) error {
	basePath := fmt.Sprintf("workspaces/%s/%s", deploymentID, componentID)
	if err := runTerraformCmd(basePath, "destroy", "-auto-approve"); err != nil {
		return fmt.Errorf("terraform destroy error: %w", err)
	}
	return nil
}
