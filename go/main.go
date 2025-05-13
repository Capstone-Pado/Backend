package main

import (
	"os"
	"pado/components"
	"pado/models"
)

// ProvisionEC2가 정의된 패키지

func main() {
	// 예제 EC2ProvisionRequest
	// req := models.EC2ProvisionRequest{
	// 	InstanceName: "crew-prod-ec2-user-api",
	// 	DeploymentID: "crew-prod",
	// 	ComponentId:  "ec2-user-api",
	//	Resion:		  "ap-northeast-2"
	// 	AMI:          "ami-0c9c942bd7bf113a2", // Ubuntu 22.04 in ap-northeast-2
	// 	InstanceType: "t3.micro",
	// 	OpenPorts:    []int{22, 80, 443, 8080},
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

	// req_mysql := models.ServiceRequest{
	// 	DeploymentID:      "crew-prod",
	// 	ServiceType:       "mysql",
	// 	ComponentId:       "mysql-user-db",
	// 	ParentComponentId: "ec2-user-api", // EC2 컴포넌트 ID

	// 	DBConfig: &models.MySQLConfig{
	// 		MySQLRootPassword: "root1234!",
	// 		MySQLDatabase:     "testdb",
	// 		MySQLUser:         "testuser",
	// 		MySQLPassword:     "testpass",
	// 		Port:              3306,
	// 	},
	// }

	// if err := components.ProvisionMySQLService(req_mysql); err != nil {
	// 	log.Fatalf("ProvisionMySQLService failed: %v", err)
	// }

	//Spring 서비스 요청 정의
	// req_svc := models.ServiceRequest{
	// 	DeploymentID:      "crew-prod",
	// 	ServiceType:       "spring",
	// 	ComponentId:       "spring-user-api",
	// 	ParentComponentId: "ec2-user-api", // 이 EC2 위에 Spring 설치

	// 	GitRepo:    "https://github.com/Capstone-Pado/Spring-Test",
	// 	DockerPort: 8080,
	// 	NginxPort:  80,
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

	req_s3 := models.S3ProvisionRequest{
		DeploymentID: "crew-prod",
		ComponentId:  "crew-s3-assets",
		BucketName:   "crew-s3-assets-test-2025",
		Region:       "ap-northeast-2",
		AWSAccessKey: os.Getenv("AWS_ACCESS_KEY"),
		AWSSecretKey: os.Getenv("AWS_SECRET_KEY"),
	}
	components.DestroyS3("crew-prod", "crew-s3-assets")
	components.ProvisionS3(req_s3)

	req_react := models.ServiceRequest{
		DeploymentID:      "crew-prod",
		ServiceType:       "react",
		ComponentId:       "react-user-web",
		GitRepo:           "https://github.com/Capstone-Pado/React-test",
		ParentComponentId: "crew-s3-assets",
	}

	req_Nodebuild := models.NodeBuildSpecTemplateData{
		S3:      req_s3,
		Service: req_react,
	}
	err := components.ProvisionReactService(req_Nodebuild)
	if err != nil {
		panic(err)
	}
}
