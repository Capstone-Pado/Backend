package com.pado.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pado.backend.domain.Component;
import com.pado.backend.domain.Project;

@Repository
public interface ComponentRepository extends JpaRepository<Component, Long>{
    List<Component> findByProject(Project project);

    // 왜 만들었는지? RESOURCE 컴포넌트 아래에 배치된 SERVICE 컴포넌트들을 조회하려고.
    List<Component> findByParentComponentId(Component parentComponent);

    // List<Component> findByParentComponentIdAndType(Long parentComponentId, String type);
    
    // 배치된 컴포넌트 검색
    @Query("SELECT c FROM Component c WHERE c.project.projectId = :projectId AND " +
        "(LOWER(c.componentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
        "LOWER(c.type) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
        "LOWER(c.subtype) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Component> searchComponentsByProjectAndKeyword(@Param("projectId") Long projectId,
                                                        @Param("keyword") String keyword);
}
