package grpc

import (
	"context"
	"pado/components"
	pb "pado/proto"
)

func (s *server) DestroyComponent(ctx context.Context, req *pb.DeploymentRequest) (*pb.StatusResponse, error) {
	if req == nil || req.DeploymentId == "" {
		return &pb.StatusResponse{
			Status: "INVALID",
			Data:   map[string]string{"message": "invalid request: DeploymentId or ComponentId is empty"},
		}, nil
	}
	for _, comp := range req.Components {
		switch component := comp.GetComponent().(type) {
		case *pb.ComponentSpec_S3:
			// S3 컴포넌트 삭제 로직 구현
			err := components.DestroyS3(req.DeploymentId, component.S3.ComponentId, nil)
			if err != nil {
				return &pb.StatusResponse{
					Status: "FAILED",
					Data:   map[string]string{"message": "failed to destroy S3 component"},
				}, err
			}
		case *pb.ComponentSpec_MySQL:
			// MySQL 컴포넌트 삭제 로직 구현
			err := components.DestroyMySQLService(req.DeploymentId, component.MySQL.ParentComponentId, component.MySQL.ComponentId, nil)
			if err != nil {
				return &pb.StatusResponse{
					Status: "FAILED",
					Data:   map[string]string{"message": "failed to destroy MySQL component"},
				}, err
			}
		case *pb.ComponentSpec_EC2:
			// EC2 컴포넌트 삭제 로직 구현
			err := components.DestroyEC2(req.DeploymentId, component.EC2.ComponentId, nil)
			if err != nil {
				return &pb.StatusResponse{
					Status: "FAILED",
					Data:   map[string]string{"message": "failed to destroy EC2 component"},
				}, err
			}
		case *pb.ComponentSpec_Spring:
			// Spring 컴포넌트 삭제 로직 구현
			err := components.DestroySpringService(req.DeploymentId, component.Spring.ParentComponentId, component.Spring.ComponentId, nil)
			if err != nil {
				return &pb.StatusResponse{
					Status: "FAILED",
					Data:   map[string]string{"message": "failed to destroy Spring component"},
				}, err
			}
		case *pb.ComponentSpec_React:
			// React 컴포넌트 삭제 로직 구현
			err := components.DestroyS3(req.DeploymentId, component.React.ParentComponentId, nil)
			if err != nil {
				return &pb.StatusResponse{
					Status: "FAILED",
					Data:   map[string]string{"message": "failed to destroy React component"},
				}, err
			}
		}
	}
	return &pb.StatusResponse{
		Status: "SUCCEEDED",
		Data:   map[string]string{"message": "Component destroyed successfully"},
	}, nil
}
