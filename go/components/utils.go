package components

import (
	"fmt"
	"os"
	"os/exec"
)

func RunTerraform(basePath string) error {
	// 1. Terraform init
	if err := runTerraformCmd(basePath, "init"); err != nil {
		return fmt.Errorf("terraform init error: %w", err)
	}

	// 2. Terraform apply
	if err := runTerraformCmd(basePath, "apply", "-auto-approve"); err != nil {
		return fmt.Errorf("terraform apply error: %w", err)
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
