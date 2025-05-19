package grpc

import (
	"fmt"
	"log"
	"net"
	pb "pado/proto"

	"google.golang.org/grpc"
)

type server struct {
	pb.UnimplementedProvisioningServiceServer
}

func StartServer(port string) error {
	lis, err := net.Listen("tcp", port)
	if err != nil {
		return fmt.Errorf("failed to listen: %v", err)
	}
	grpcServer := grpc.NewServer()
	pb.RegisterProvisioningServiceServer(grpcServer, &server{})
	log.Printf("gRPC server listening on port %s", port)
	return grpcServer.Serve(lis)
}
