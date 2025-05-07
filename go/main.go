package main

import (
	"pado/components"
	// ProvisionEC2가 정의된 패키지
)

func main() {
	// 예제 EC2ProvisionRequest
	// req := models.EC2ProvisionRequest{
	// 	InstanceName: "crew-prod-ec2-user-api",
	// 	DeploymentID: "crew-prod",
	// 	ComponentId:  "ec2-user-api",
	// 	AMI:          "ami-0c9c942bd7bf113a2", // Ubuntu 22.04 in ap-northeast-2
	// 	InstanceType: "t2.micro",
	// 	OpenPorts:    []int{22, 80, 443},
	// 	AWSAccessKey: os.Getenv("AWS_ACCESS_KEY"),
	// 	AWSSecretKey: os.Getenv("AWS_SECRET_KEY"),
	// }

	// // JSON 출력 (디버깅 용도)
	// if b, err := json.MarshalIndent(req, "", "  "); err == nil {
	// 	fmt.Println("Request:")
	// 	fmt.Println(string(b))
	// }

	// // EC2 프로비저닝
	// if err := components.ProvisionEC2(req); err != nil {
	// 	log.Fatalf("ProvisionEC2 failed: %v", err)
	// }

	// Spring 서비스 요청 정의
	// req_svc := models.ServiceRequest{
	// 	DeploymentID:      "crew-prod",
	// 	ServiceType:       "spring",
	// 	ComponentId:       "spring-user-api",
	// 	ParentComponentId: "ec2-user-api", // 이 EC2 위에 Spring 설치

	// 	GitRepo:    "https://github.com/BuntyRaghani/spring-boot-hello-world",
	// 	DockerPort: 8080,
	// 	BuildTool:  "gradle",
	// 	JDKVersion: "17",
	// 	Env: map[string]string{
	// 		"EXAMPLE_ENV": "test",
	// 	},
	// }

	// fmt.Println("✅ EC2 provision complete.")

	// if err := components.ProvisionSpringService(req_svc); err != nil {
	// 	log.Fatalf("ProvisionSpringService failed: %v", err)
	// }

	// fmt.Println("✅ Spring service provisioned successfully.")
	components.DestroyEC2("crew-prod", "ec2-user-api")
}
