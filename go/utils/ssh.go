package utils

import (
	"fmt"
	"os"
	"os/exec"
)

func RunRemoteScript(ip, user, keyPath, localScriptPath string, logFile *os.File) error {
	remotePath := "/home/" + user + "/init.sh"

	// 1. scp로 복사
	scpCmd := exec.Command("scp",
		"-i", keyPath,
		"-o", "StrictHostKeyChecking=no",
		localScriptPath,
		fmt.Sprintf("%s@%s:%s", user, ip, remotePath),
	)
	scpCmd.Stdout = logFile
	scpCmd.Stderr = logFile
	if err := scpCmd.Run(); err != nil {
		return fmt.Errorf("scp error: %w", err)
	}

	// 2. ssh로 실행
	sshCmd := exec.Command("ssh",
		"-i", keyPath,
		"-o", "StrictHostKeyChecking=no",
		fmt.Sprintf("%s@%s", user, ip),
		fmt.Sprintf("chmod +x %s && sudo bash %s", remotePath, remotePath),
	)
	sshCmd.Stdout = logFile
	sshCmd.Stderr = logFile
	if err := sshCmd.Run(); err != nil {
		return fmt.Errorf("ssh exec error: %w", err)
	}

	return nil
}

func RunRemoteCommand(ip, user, keyPath, command string, logFile *os.File) error {
	sshCmd := exec.Command("ssh",
		"-i", keyPath,
		"-o", "StrictHostKeyChecking=no",
		fmt.Sprintf("%s@%s", user, ip),
		fmt.Sprintf("sudo bash -c '%s'", command),
	)

	sshCmd.Stdout = logFile
	sshCmd.Stderr = logFile

	if err := sshCmd.Run(); err != nil {
		return fmt.Errorf("ssh exec error: %w", err)
	}

	return nil
}
