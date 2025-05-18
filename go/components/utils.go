package components

import (
	"fmt"
	"os"
	"os/exec"
	"path/filepath"
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
	logFilePath := filepath.Join(dir, "provision.log")
	logFile, err := os.OpenFile(logFilePath, os.O_CREATE|os.O_WRONLY|os.O_APPEND, 0644)
	if err != nil {
		return err
	}
	defer logFile.Close()
	cmd := exec.Command("terraform", args...)
	cmd.Dir = dir
	cmd.Stdout = logFile
	cmd.Stderr = logFile
	return cmd.Run()
}
