package com.pado.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pado.backend.domain.Component;
import com.pado.backend.domain.Project;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long>{
    List<Component> findByProject(Project project);
}
