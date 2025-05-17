package com.pado.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pado.backend.domain.Project;
import com.pado.backend.domain.User;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>{
    public List<Project> findByUser(User user);
}
