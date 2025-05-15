package provision

import (
	"pado/components"
	"pado/models"
)

func S3ReactProvision(reqS3 models.S3ProvisionRequest, reqReact models.ServiceRequest) error {
	if err := components.ProvisionS3(reqS3); err != nil {
		components.DestroyS3(reqS3.DeploymentID, reqS3.ComponentId)
		return err
	}
	nodeBuild := models.NodeBuildSpecTemplateData{
		S3:      reqS3,
		Service: reqReact,
	}
	if err := components.ProvisionReactService(nodeBuild); err != nil {
		components.DestroyS3(reqS3.DeploymentID, reqS3.ComponentId)
		return err
	}
	return nil
}
