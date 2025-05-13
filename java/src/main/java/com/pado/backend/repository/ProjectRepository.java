package com.pado.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pado.backend.domain.Project;
import com.pado.backend.domain.User;

public interface ProjectRepository extends JpaRepository<Project, Long>{
    public List<Project> findByUser(User user);
}
