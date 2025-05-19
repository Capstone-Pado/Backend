package components

import (
	"bufio"
	"fmt"
	"io"
	"os"
	"os/exec"
	"time"
)

func RunTerraform(basePath string, logFile *os.File) error {
	// 1. Terraform init
	if err := runTerraformCmd(basePath, logFile, "init"); err != nil {
		return fmt.Errorf("terraform init error: %w", err)
	}

	// 2. Terraform apply
	if err := runTerraformCmd(basePath, logFile, "apply", "-auto-approve"); err != nil {
		return fmt.Errorf("terraform apply error: %w", err)
	}
	return nil
}

func runTerraformCmd(dir string, logFile *os.File, args ...string) error {
	cmd := exec.Command("terraform", args...)
	cmd.Dir = dir

	stdout, _ := cmd.StdoutPipe()
	stderr, _ := cmd.StderrPipe()

	if err := cmd.Start(); err != nil {
		return err
	}

	go writeTaggedOutput(stdout, "[INFO]", logFile)
	go writeTaggedOutput(stderr, "[ERROR]", logFile)

	return cmd.Wait()
}

func writeTaggedOutput(reader io.Reader, tag string, logFile *os.File) {
	scanner := bufio.NewScanner(reader)
	for scanner.Scan() {
		line := scanner.Text()
		timestamp := time.Now().Format("2006-01-02 15:04:05")
		formatted := fmt.Sprintf("%s %s %s\n", timestamp, tag, line)
		logFile.WriteString(formatted)
	}
}

func GetComponentStatus(deploymentId string, componentId string) string {
	statusPath := fmt.Sprintf("workspaces/%s/%s/status.log", deploymentId, componentId)
	data, err := os.ReadFile(statusPath)
	if err != nil {
		return "UNKNOWN"
	}
	return string(data)
}

func RollbackTerraformResources(resolved map[string]interface{}, deploymentId string, logFile *os.File) error {

	for componentId := range resolved {

		basePath := fmt.Sprintf("workspaces/%s/%s", deploymentId, componentId)
		if err := runTerraformCmd(basePath, logFile, "destroy", "-auto-approve"); err != nil {
			return fmt.Errorf("terraform destroy error: %w", err)
		}
	}
	return nil
}
