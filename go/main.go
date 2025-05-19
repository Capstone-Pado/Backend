package main

import (
	"log"
	"pado/grpc"
)

func main() {
	err := grpc.StartServer(":50051")
	if err != nil {
		log.Fatalf("failed to start gRPC server: %v", err)
	}
}
