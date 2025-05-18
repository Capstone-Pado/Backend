package main

import (
	"bufio"
	"context"
	"fmt"
	"io"
	"log"
	"net"
	"os"
	"pado/models"
	pb "pado/proto"
	"pado/provision"
	"strings"
	"time"

	"google.golang.org/grpc"
)

type server struct {
	pb.UnimplementedProvisioningServiceServer
}

func (s *server) StartEC2Spring(ctx context.Context, req *pb.StartEC2SpringRequest) (*pb.ProvisionStartResponse, error) {
	// EC2 인스턴스 시작 로직 구현
	if req == nil || req.EC2 == nil || req.Spring == nil {
		return &pb.ProvisionStartResponse{
			Status: "Error",
			Data:   map[string]string{"message": "invalid request: EC2 or Spring field is nil"},
		}, nil
	}
	EC2ProvisionRequest := models.EC2ProvisionRequest{
		DeploymentId: req.DeploymentId,
		ComponentId:  req.EC2.ComponentId,
		InstanceName: req.EC2.InstanceName,
		InstanceType: req.EC2.InstanceType,
		Region:       req.EC2.Region,
		AMI:          req.EC2.AMI,
		OpenPorts:    req.EC2.OpenPorts,
		AWSAccessKey: req.EC2.AWSAccessKey,
		AWSSecretKey: req.EC2.AWSSecretKey,
	}
	SpringProvisionRequest := models.ServiceRequest{
		DeploymentId:      req.DeploymentId,
		ServiceType:       "spring",
		ComponentId:       req.Spring.ComponentId,
		GitRepo:           req.Spring.GitRepo,
		DockerPort:        req.Spring.DockerPort,
		NginxPort:         req.Spring.NginxPort,
		BuildTool:         req.Spring.BuildTool,
		JDKVersion:        req.Spring.JDKVersion,
		Env:               req.Spring.Env,
		ParentComponentId: req.Spring.ParentComponentId,
	}

	go func() {
		err := provision.EC2SpringProvision(EC2ProvisionRequest, SpringProvisionRequest)
		statusPath := fmt.Sprintf("workspaces/%s/status.log", req.DeploymentId)
		var status string
		if err != nil {
			status = "FAILED"
		} else {
			status = "SUCCEEDED"
		}
		_ = os.WriteFile(statusPath, []byte(status), 0644)
	}()

	return &pb.ProvisionStartResponse{
		Status: "STARTED",
		Data:   map[string]string{"message": "Provisioning started asynchronously"},
	}, nil
}

func (s *server) StartEC2MySQL(ctx context.Context, req *pb.StartEC2MySQLRequest) (*pb.ProvisionStartResponse, error) {
	// EC2 인스턴스 시작 로직 구현
	if req == nil || req.EC2 == nil || req.MySQL == nil {
		return &pb.ProvisionStartResponse{
			Status: "Error",
			Data:   map[string]string{"message": "invalid request: EC2 or MySQL field is nil"},
		}, nil
	}
	EC2ProvisionRequest := models.EC2ProvisionRequest{
		DeploymentId: req.DeploymentId,
		ComponentId:  req.EC2.ComponentId,
		InstanceName: req.EC2.InstanceName,
		InstanceType: req.EC2.InstanceType,
		Region:       req.EC2.Region,
		AMI:          req.EC2.AMI,
		OpenPorts:    req.EC2.OpenPorts,
		AWSAccessKey: req.EC2.AWSAccessKey,
		AWSSecretKey: req.EC2.AWSSecretKey,
	}
	MySQLProvisionRequest := models.ServiceRequest{
		DeploymentId:      req.DeploymentId,
		ServiceType:       "mysql",
		ComponentId:       req.MySQL.ComponentId,
		ParentComponentId: req.MySQL.ParentComponentId,
		DBConfig: &models.MySQLConfig{
			MySQLRootPassword: req.MySQL.MySQLRootPassword,
			MySQLDatabase:     req.MySQL.MySQLDatabase,
			MySQLUser:         req.MySQL.MySQLUser,
			MySQLPassword:     req.MySQL.MySQLPassword,
			Port:              req.MySQL.Port,
		},
	}

	go func() {
		err := provision.EC2MySQLProvision(EC2ProvisionRequest, MySQLProvisionRequest)
		statusPath := fmt.Sprintf("workspaces/%s/status.log", req.DeploymentId)
		var status string
		if err != nil {
			status = "FAILED"
		} else {
			status = "SUCCEEDED"
		}
		_ = os.WriteFile(statusPath, []byte(status), 0644)
	}()

	return &pb.ProvisionStartResponse{
		Status: "STARTED",
		Data:   map[string]string{"message": "Provisioning started asynchronously"},
	}, nil
}

func (s *server) StartS3React(ctx context.Context, req *pb.StartS3ReactRequest) (*pb.ProvisionStartResponse, error) {
	// S3 React 시작 로직 구현
	if req == nil || req.S3 == nil || req.React == nil {
		return &pb.ProvisionStartResponse{
			Status: "Error",
			Data:   map[string]string{"message": "invalid request: S3 or React field is nil"},
		}, nil
	}
	S3ProvisionRequest := models.S3ProvisionRequest{
		DeploymentId: req.DeploymentId,
		ComponentId:  req.S3.ComponentId,
		BucketName:   req.S3.BucketName,
		Region:       req.S3.Region,
		AWSAccessKey: req.S3.AWSAccessKey,
		AWSSecretKey: req.S3.AWSSecretKey,
	}
	ReactProvisionRequest := models.ServiceRequest{
		DeploymentId:      req.DeploymentId,
		ServiceType:       "react",
		ComponentId:       req.React.ComponentId,
		GitRepo:           req.React.GitRepo,
		ParentComponentId: req.React.ParentComponentId,
	}

	go func() {
		err := provision.S3ReactProvision(S3ProvisionRequest, ReactProvisionRequest)
		statusPath := fmt.Sprintf("workspaces/%s/status.log", req.DeploymentId)
		var status string
		if err != nil {
			status = "FAILED"
		} else {
			status = "SUCCEEDED"
		}
		_ = os.WriteFile(statusPath, []byte(status), 0644)
	}()

	return &pb.ProvisionStartResponse{
		Status: "STARTED",
		Data:   map[string]string{"message": "Provisioning started asynchronously"},
	}, nil
}

func (s *server) StreamProvisionLogs(req *pb.ProvisionLogRequest, stream pb.ProvisioningService_StreamProvisionLogsServer) error {
	if req == nil || req.DeploymentId == "" || req.ComponentId == "" {
		return fmt.Errorf("invalid request: DeploymentId or ComponentId is empty")
	}

	// 로그 파일 경로 지정
	logPath := fmt.Sprintf("workspaces/%s/%s/provision.log", req.DeploymentId, req.ComponentId)

	// 로그 파일 열기 (실시간 tail 처리를 위해 Seek)
	file, err := os.Open(logPath)
	if err != nil {
		return fmt.Errorf("failed to open log file: %w", err)
	}
	defer file.Close()

	// 파일 끝으로 이동
	reader := bufio.NewReader(file)
	idleStart := time.Now()
	for {
		// 한 줄씩 읽기
		line, err := reader.ReadString('\n')
		if err != nil {
			if err == io.EOF {
				if time.Since(idleStart) > time.Minute {
					return nil
				} else {
					idleStart = time.Now()
				}
				time.Sleep(500 * time.Millisecond)
				continue
			}
			return fmt.Errorf("error reading log file: %w", err)
		}

		// 클라이언트로 전송
		if err := stream.Send(&pb.ProvisionLog{
			ComponentId: req.ComponentId,
			LogLine:     strings.TrimSpace(line),
		}); err != nil {
			return fmt.Errorf("failed to send log line: %w", err)
		}
	}
}

func main() {
	lis, err := net.Listen("tcp", ":50051")
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}
	grpcServer := grpc.NewServer()
	pb.RegisterProvisioningServiceServer(grpcServer, &server{})
	log.Println("gRPC server listening on port 50051")
	grpcServer.Serve(lis)
}
