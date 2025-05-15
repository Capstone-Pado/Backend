package main

import (
	"context"
	"fmt"
	"log"
	"net"
	"pado/models"
	pb "pado/proto"
	"pado/provision"

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
		DeploymentID: req.DeploymentID,
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
		DeploymentID:      req.DeploymentID,
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
		if err != nil {
			fmt.Printf("Provisioning failed: %v\n", err)
		} else {
			// 성공 시 상태 업데이트
		}
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
		DeploymentID: req.DeploymentID,
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
		DeploymentID:      req.DeploymentID,
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
		if err != nil {
			fmt.Printf("Provisioning failed: %v\n", err)
		} else {
			// 성공 시 상태 업데이트
		}
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
		DeploymentID: req.DeploymentID,
		ComponentId:  req.S3.ComponentId,
		BucketName:   req.S3.BucketName,
		Region:       req.S3.Region,
		AWSAccessKey: req.S3.AWSAccessKey,
		AWSSecretKey: req.S3.AWSSecretKey,
	}
	ReactProvisionRequest := models.ServiceRequest{
		DeploymentID:      req.DeploymentID,
		ServiceType:       "react",
		ComponentId:       req.React.ComponentId,
		GitRepo:           req.React.GitRepo,
		ParentComponentId: req.React.ParentComponentId,
	}

	go func() {
		err := provision.S3ReactProvision(S3ProvisionRequest, ReactProvisionRequest)
		if err != nil {
			fmt.Printf("Provisioning failed: %v\n", err)
		} else {
			// 성공 시 상태 업데이트
		}
	}()

	return &pb.ProvisionStartResponse{
		Status: "STARTED",
		Data:   map[string]string{"message": "Provisioning started asynchronously"},
	}, nil
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
