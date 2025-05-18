package utils

import (
	"bytes"
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"encoding/json"
	"encoding/pem"
	"fmt"
	"net"
	"os/exec"
	"path/filepath"
	"time"

	"golang.org/x/crypto/ssh"
)

func GetResourceIP(deploymentId, componentId string) (string, error) {
	// Terraform 작업 디렉토리
	tfDir := filepath.Join("workspaces", deploymentId, componentId)

	// terraform output -json 실행
	cmd := exec.Command("terraform", "output", "-json")
	cmd.Dir = tfDir

	var out bytes.Buffer
	cmd.Stdout = &out

	if err := cmd.Run(); err != nil {
		return "", fmt.Errorf("failed to run terraform output: %w", err)
	}

	// 결과 파싱
	var result map[string]struct {
		Value string `json:"value"`
	}

	if err := json.Unmarshal(out.Bytes(), &result); err != nil {
		return "", fmt.Errorf("failed to parse terraform output: %w", err)
	}

	// instance_ip 또는 public_ip 같은 키 사용 (사용자 정의 따라 변경 가능)
	ip, ok := result["instance_ip"]
	if !ok {
		return "", fmt.Errorf("instance_ip not found in output")
	}

	return ip.Value, nil
}

func GenerateRSAKeyPair() (publicKey string, privateKey []byte, err error) {
	priv, err := rsa.GenerateKey(rand.Reader, 4096)
	if err != nil {
		return "", nil, err
	}

	pub, err := ssh.NewPublicKey(&priv.PublicKey)
	if err != nil {
		return "", nil, err
	}

	pubStr := string(ssh.MarshalAuthorizedKey(pub))
	privPEM := pem.EncodeToMemory(&pem.Block{
		Type:  "RSA PRIVATE KEY",
		Bytes: x509.MarshalPKCS1PrivateKey(priv),
	})

	return pubStr, privPEM, nil
}

func GetS3Domain(deploymentId, componentId string) (string, error) {
	// Terraform 작업 디렉토리
	tfDir := filepath.Join("workspaces", deploymentId, componentId)

	// terraform output -json 실행
	cmd := exec.Command("terraform", "output", "-json")
	cmd.Dir = tfDir

	var out bytes.Buffer
	cmd.Stdout = &out

	if err := cmd.Run(); err != nil {
		return "", fmt.Errorf("failed to run terraform output: %w", err)
	}

	// 결과 파싱
	var result map[string]struct {
		Value string `json:"value"`
	}

	if err := json.Unmarshal(out.Bytes(), &result); err != nil {
		return "", fmt.Errorf("failed to parse terraform output: %w", err)
	}

	// bucket_domain_name 같은 키 사용 (사용자 정의 따라 변경 가능)
	domain, ok := result["bucket_domain_name"]
	if !ok {
		return "", fmt.Errorf("bucket_domain_name not found in output")
	}

	return domain.Value, nil
}

func WaitForSSH(ip string, port int, timeout time.Duration) error {
	deadline := time.Now().Add(timeout)
	for time.Now().Before(deadline) {
		conn, err := net.DialTimeout("tcp", fmt.Sprintf("%s:%d", ip, port), 3*time.Second)
		if err == nil {
			_ = conn.Close()
			return nil // 연결 성공
		}
		time.Sleep(2 * time.Second)
	}
	return fmt.Errorf("SSH not ready after %v", timeout)
}
