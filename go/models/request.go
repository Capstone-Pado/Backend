package models

type EC2ProvisionRequest struct {
	DeploymentID string `json:"user_id"`
	ComponentId  string `json:"component_id"`
	InstanceName string `json:"instance_name"` // EC2 인스턴스 이름
	InstanceType string `json:"instance_type"`
	Region       string `json:"region"` // S3 버킷 리전
	AMI          string `json:"ami"`
	KeyName      string `json:"key_name"`
	OpenPorts    []int  `json:"open_ports"`
	PublicKey    string `json:"public_key"` // SSH 공개키
	AWSAccessKey string `json:"aws_access_key"`
	AWSSecretKey string `json:"aws_secret_key"`
}

type NodeBuildSpecTemplateData struct {
	S3      S3ProvisionRequest
	Service ServiceRequest
}

type S3ProvisionRequest struct {
	DeploymentID string `json:"user_id"`
	ComponentId  string `json:"component_id"`
	BucketName   string `json:"bucket_name"` // S3 버킷 이름
	Region       string `json:"region"`      // S3 버킷 리전
	AWSAccessKey string `json:"aws_access"`
	AWSSecretKey string `json:"aws_secret"`
}

type ServiceRequest struct {
	DeploymentID      string `json:"deployment_id"`
	ServiceType       string `json:"service_type"` // "spring", "mysql", etc.
	ComponentId       string `json:"component_id"`
	ParentComponentId string `json:"parent_component_id"` // 부모 컴포넌트 ID (예: EC2 ID)

	// Spring 전용
	GitRepo    string            `json:"git_repo"`
	DockerPort int               `json:"docker_port"`
	NginxPort  int               `json:"nginx_port"` // 예: 8080, 8081 등
	BuildTool  string            `json:"build_tool"` // "gradle" or "maven"
	JDKVersion string            `json:"jdk_version"`
	Env        map[string]string `json:"env"` // 환경변수

	// MySQL 전용
	DBConfig *MySQLConfig `json:"db_config,omitempty"`
}

type MySQLConfig struct {
	MySQLRootPassword string `json:"mysql_root_password"`
	MySQLDatabase     string `json:"mysql_database"`
	MySQLUser         string `json:"mysql_user"`
	MySQLPassword     string `json:"mysql_password"`
	Port              int    `json:"port"`
}
