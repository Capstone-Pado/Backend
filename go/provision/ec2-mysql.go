package provision

import (
	"pado/components"
	"pado/models"
)

func EC2MySQLProvision(reqEC2 models.EC2ProvisionRequest, reqMySQL models.ServiceRequest) error {
	if err := components.ProvisionEC2(reqEC2); err != nil {
		components.DestroyEC2(reqEC2.DeploymentId, reqEC2.ComponentId)
		return err
	}
	if err := components.ProvisionMySQLService(reqMySQL); err != nil {
		components.DestroyEC2(reqEC2.DeploymentId, reqEC2.ComponentId)
		return err
	}
	return nil
}
