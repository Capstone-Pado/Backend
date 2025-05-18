package provision

import (
	"pado/components"
	"pado/models"
)

func EC2SpringProvision(reqEC2 models.EC2ProvisionRequest, reqSpring models.ServiceRequest) error {
	if err := components.ProvisionEC2(reqEC2); err != nil {
		components.DestroyEC2(reqEC2.DeploymentId, reqEC2.ComponentId)
		return err
	}
	if err := components.ProvisionSpringService(reqSpring); err != nil {
		components.DestroyEC2(reqEC2.DeploymentId, reqEC2.ComponentId)
		return err
	}
	return nil
}
