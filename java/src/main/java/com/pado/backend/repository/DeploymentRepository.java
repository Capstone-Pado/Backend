package com.pado.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pado.backend.domain.Deployment;
import com.pado.backend.domain.Project;

@Repository
public interface DeploymentRepository extends JpaRepository<Deployment, Long>{
    public List<Deployment> findByProject(Project project);
}
