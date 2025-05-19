package grpc

import (
	"bufio"
	"context"
	"fmt"
	"io"
	"log"
	"os"
	"pado/components"
	"pado/models"
	pb "pado/proto"
	"strings"
	"time"
)

func calculateCost(req *pb.DeploymentRequest) (string, string) {
	// 비용 계산 로직 구현
	var ec2PriceTable = map[string]float64{
		"t3.micro":  0.0116 * 730,
		"t3.small":  0.023 * 730,
		"t3.medium": 0.0464 * 730,
		"t2.micro":  0.0128 * 730,
	}
	cost_EC2 := 0.0
	cost_S3 := 0.0
	for _, comp := range req.Components {
		switch component := comp.GetComponent().(type) {
		case *pb.ComponentSpec_EC2:
			instanceType := component.EC2.InstanceType
			if price, ok := ec2PriceTable[instanceType]; ok {
				cost_EC2 += price
			}
		case *pb.ComponentSpec_S3:
			cost_S3 += 0.05 // S3 비용 예시
		}
	}
	return fmt.Sprintf("%.2f", cost_EC2), fmt.Sprintf("%.2f", cost_S3)
}

func (s *server) PlanDeploy(ctx context.Context, req *pb.DeploymentRequest) (*pb.CostResponse, error) {
	if req == nil || req.DeploymentId == "" {
		return &pb.CostResponse{
			Cost: "INVALID",
			Data: map[string]string{"message": "invalid request: DeploymentId is empty"},
		}, nil
	}
	if len(req.Components) == 0 {
		return &pb.CostResponse{
			Cost: "INVALID",
			Data: map[string]string{"message": "invalid request: Components is empty"},
		}, nil
	}
	for _, comp := range req.Components {
		fmt.Println(req.Components)
		switch component := comp.GetComponent().(type) {
		case *pb.ComponentSpec_EC2:
			// EC2 컴포넌트 배포 로직
			EC2ProvisionRequest := models.EC2ProvisionRequest{
				DeploymentId: req.DeploymentId,
				ComponentId:  component.EC2.ComponentId,
				InstanceName: component.EC2.InstanceName,
				InstanceType: component.EC2.InstanceType,
				Region:       component.EC2.Region,
				AMI:          component.EC2.AMI,
				OpenPorts:    component.EC2.OpenPorts,
				AWSAccessKey: component.EC2.AWSAccessKey,
				AWSSecretKey: component.EC2.AWSSecretKey,
			}
			err := components.PlanProvisionEC2(EC2ProvisionRequest)
			if err != nil {
				return &pb.CostResponse{
					Cost: "FAILED",
					Data: map[string]string{"message": fmt.Sprintf("EC2 provisioning failed: %s", err)},
				}, nil
			}
		case *pb.ComponentSpec_S3:
			// S3 컴포넌트 배포 로직
			S3ProvisionRequest := models.S3ProvisionRequest{
				DeploymentId: req.DeploymentId,
				ComponentId:  component.S3.ComponentId,
				BucketName:   component.S3.BucketName,
				Region:       component.S3.Region,
				AWSAccessKey: component.S3.AWSAccessKey,
				AWSSecretKey: component.S3.AWSSecretKey,
			}
			err := components.PlanProvisionS3(S3ProvisionRequest)
			if err != nil {
				return &pb.CostResponse{
					Cost: "FAILED",
					Data: map[string]string{"message": fmt.Sprintf("S3 provisioning failed: %s", err)},
				}, nil
			}
		default:
			continue
		}
	}
	cost_EC2, cost_S3 := calculateCost(req)
	return &pb.CostResponse{
		Cost: "SUCCEEDED",
		Data: map[string]string{
			"EC2": cost_EC2,
			"S3":  cost_S3,
		},
	}, nil
}

func (s *server) Deploy(req *pb.DeploymentRequest, stream pb.ProvisioningService_DeployServer) error {
	resolvedComponents := map[string]interface{}{}
	if req == nil || req.DeploymentId == "" {
		return fmt.Errorf("invalid request: DeploymentId is empty")
	}
	if len(req.Components) == 0 {
		return fmt.Errorf("invalid request: Components is empty")
	}
	logPath := fmt.Sprintf("workspaces/%s/provision.log", req.DeploymentId)
	logFile, err := os.OpenFile(logPath, os.O_CREATE|os.O_WRONLY|os.O_TRUNC, 0644)
	if err != nil {
		return fmt.Errorf("failed to open log file: %w", err)
	}
	go func() {
		file, err := os.Open(logPath)
		if err != nil {
			log.Printf("failed to open log file: %v", err)
			return
		}
		defer file.Close()

		reader := bufio.NewReader(file)
		file.Seek(0, io.SeekEnd) // 파일 끝으로 이동

		for {
			line, err := reader.ReadString('\n')
			if err != nil {
				if err == io.EOF {
					time.Sleep(200 * time.Millisecond)
					continue
				}
				log.Printf("log read error: %v", err)
				break
			}

			err = stream.Send(&pb.DeployLog{
				DeploymentId: req.DeploymentId,
				ComponentId:  "",
				LogLine:      strings.TrimSpace(line),
			})
			if err != nil {
				log.Printf("stream send error: %v", err)
				break
			}
		}

	}()
	for _, comp := range req.Components {
		// 각 컴포넌트에 대한 배포 로직 구현
		switch component := comp.GetComponent().(type) {
		case *pb.ComponentSpec_EC2:
			// EC2 컴포넌트 배포 로직
			EC2ProvisionRequest := models.EC2ProvisionRequest{
				DeploymentId: req.DeploymentId,
				ComponentId:  component.EC2.ComponentId,
				InstanceName: component.EC2.InstanceName,
				InstanceType: component.EC2.InstanceType,
				Region:       component.EC2.Region,
				AMI:          component.EC2.AMI,
				OpenPorts:    component.EC2.OpenPorts,
				AWSAccessKey: component.EC2.AWSAccessKey,
				AWSSecretKey: component.EC2.AWSSecretKey,
			}
			err := components.ProvisionEC2(EC2ProvisionRequest, logFile)
			resolvedComponents[component.EC2.ComponentId] = EC2ProvisionRequest
			if err != nil {
				components.RollbackTerraformResources(resolvedComponents, req.DeploymentId, logFile)
				stream.Send(&pb.DeployLog{
					DeploymentId: req.DeploymentId,
					ComponentId:  component.EC2.ComponentId,
					LogLine:      fmt.Sprintf("[ERROR] EC2 provisioning failed: %s", err),
				})
				return nil
			}
		case *pb.ComponentSpec_S3:
			// S3 컴포넌트 배포 로직
			S3ProvisionRequest := models.S3ProvisionRequest{
				DeploymentId: req.DeploymentId,
				ComponentId:  component.S3.ComponentId,
				BucketName:   component.S3.BucketName,
				Region:       component.S3.Region,
				AWSAccessKey: component.S3.AWSAccessKey,
				AWSSecretKey: component.S3.AWSSecretKey,
			}
			err := components.ProvisionS3(S3ProvisionRequest, logFile)
			resolvedComponents[component.S3.ComponentId] = S3ProvisionRequest
			if err != nil {
				components.RollbackTerraformResources(resolvedComponents, req.DeploymentId, logFile)
				stream.Send(&pb.DeployLog{
					DeploymentId: req.DeploymentId,
					ComponentId:  component.S3.ComponentId,
					LogLine:      fmt.Sprintf("[ERROR] S3 provisioning failed: %s", err),
				})
				return nil
			}
		case *pb.ComponentSpec_Spring:
			// Spring 컴포넌트 배포 로직
			SpringProvisionRequest := models.ServiceRequest{
				DeploymentId:      req.DeploymentId,
				ServiceType:       "spring",
				ComponentId:       component.Spring.ComponentId,
				GitRepo:           component.Spring.GitRepo,
				DockerPort:        component.Spring.DockerPort,
				NginxPort:         component.Spring.NginxPort,
				BuildTool:         component.Spring.BuildTool,
				JDKVersion:        component.Spring.JDKVersion,
				Env:               component.Spring.Env,
				ParentComponentId: component.Spring.ParentComponentId,
			}
			err := components.ProvisionSpringService(SpringProvisionRequest, logFile)
			if err != nil {
				// Spring 도커 삭제 로직 필요함.
				//components.DestroySpringService(req.DeploymentId, component.Spring.ComponentId)
				components.RollbackTerraformResources(resolvedComponents, req.DeploymentId, logFile)
				stream.Send(&pb.DeployLog{
					DeploymentId: req.DeploymentId,
					ComponentId:  component.Spring.ComponentId,
					LogLine:      fmt.Sprintf("[ERROR] Spring provisioning failed: %s", err),
				})
				return nil
			}
		case *pb.ComponentSpec_React:
			// React 컴포넌트 배포 로직
			ReactProvisionRequest := models.ServiceRequest{
				DeploymentId:      req.DeploymentId,
				ServiceType:       "react",
				ComponentId:       component.React.ComponentId,
				GitRepo:           component.React.GitRepo,
				ParentComponentId: component.React.ParentComponentId,
			}
			parentS3, ok := resolvedComponents[component.React.ParentComponentId].(models.S3ProvisionRequest)
			if !ok {
				components.RollbackTerraformResources(resolvedComponents, req.DeploymentId, logFile)
				stream.Send(&pb.DeployLog{
					DeploymentId: req.DeploymentId,
					ComponentId:  component.React.ComponentId,
					LogLine:      "[ERROR] Parent S3 component not found",
				})
				return nil
			}
			NodeBuildSpecTemplateData := models.NodeBuildSpecTemplateData{
				S3:      parentS3,
				Service: ReactProvisionRequest,
			}
			err := components.ProvisionReactService(NodeBuildSpecTemplateData, logFile)
			if err != nil {
				// React 도커 삭제 로직 필요함.
				//components.DestroyReactService(req.DeploymentId, component.React.ComponentId)
				components.RollbackTerraformResources(resolvedComponents, req.DeploymentId, logFile)
				stream.Send(&pb.DeployLog{
					DeploymentId: req.DeploymentId,
					ComponentId:  component.React.ComponentId,
					LogLine:      fmt.Sprintf("[ERROR] React provisioning failed: %s", err),
				})
				return nil
			}
		case *pb.ComponentSpec_MySQL:
			// MySQL 컴포넌트 배포 로직
			MySQLProvisionRequest := models.ServiceRequest{
				DeploymentId:      req.DeploymentId,
				ServiceType:       "mysql",
				ComponentId:       component.MySQL.ComponentId,
				ParentComponentId: component.MySQL.ParentComponentId,
				DBConfig: &models.MySQLConfig{
					MySQLRootPassword: component.MySQL.MySQLRootPassword,
					MySQLDatabase:     component.MySQL.MySQLDatabase,
					MySQLUser:         component.MySQL.MySQLUser,
					MySQLPassword:     component.MySQL.MySQLPassword,
					Port:              component.MySQL.Port,
				},
			}
			err := components.ProvisionMySQLService(MySQLProvisionRequest, logFile)
			if err != nil {
				// MySQL 도커 삭제 로직 필요함.
				//components.DestroyMySQLService(req.DeploymentId, component.MySQL.ComponentId)
				components.RollbackTerraformResources(resolvedComponents, req.DeploymentId, logFile)
				stream.Send(&pb.DeployLog{
					DeploymentId: req.DeploymentId,
					ComponentId:  component.MySQL.ComponentId,
					LogLine:      fmt.Sprintf("[ERROR] MySQL provisioning failed: %s", err),
				})
				return nil
			}
		}
	}
	return nil
}

func (s *server) StopDeploy(ctx context.Context, req *pb.DeploymentRequest) (*pb.StatusResponse, error) {

	logPath := fmt.Sprintf("workspaces/%s/destroy.log", req.DeploymentId)
	logFile, err := os.OpenFile(logPath, os.O_CREATE|os.O_WRONLY|os.O_TRUNC, 0644)
	if err != nil {
		return &pb.StatusResponse{
			Status: "FAILED",
			Data:   map[string]string{"message": fmt.Sprintf("failed to open log file: %s", err)},
		}, nil
	}
	if req == nil || req.DeploymentId == "" {
		return &pb.StatusResponse{
			Status: "INVALID",
			Data:   map[string]string{"message": "invalid request: DeploymentId is empty"},
		}, nil
	}
	if len(req.Components) == 0 {
		return &pb.StatusResponse{
			Status: "INVALID",
			Data:   map[string]string{"message": "invalid request: Components is empty"},
		}, nil
	}
	for _, comp := range req.Components {
		switch component := comp.GetComponent().(type) {
		case *pb.ComponentSpec_EC2:
			err := components.DestroyEC2(req.DeploymentId, component.EC2.ComponentId, logFile)
			if err != nil {
				return &pb.StatusResponse{
					Status: "FAILED",
					Data:   map[string]string{"message": fmt.Sprintf("EC2 destruction failed: %s", err)},
				}, nil
			}
		case *pb.ComponentSpec_S3:
			err := components.DestroyS3(req.DeploymentId, component.S3.ComponentId, logFile)
			if err != nil {
				return &pb.StatusResponse{
					Status: "FAILED",
					Data:   map[string]string{"message": fmt.Sprintf("S3 destruction failed: %s", err)},
				}, nil
			}
		}
	}
	return &pb.StatusResponse{
		Status: "SUCCEEDED",
		Data:   map[string]string{"message": "Components destroyed successfully"},
	}, nil
}
