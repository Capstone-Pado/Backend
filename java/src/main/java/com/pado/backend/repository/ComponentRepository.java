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

    // 부모 컴포넌트 ID와 타입 기준으로 자식 컴포넌트 조회 (SERVICE 타입 자식용)
    List<Component> findByParentComponentId(Long parentComponentId);

    // List<Component> findByParentComponentIdAndType(Long parentComponentId, String type);
    
    // 배치된 컴포넌트 검색
    @Query("SELECT c FROM Component c WHERE c.project.projectId = :projectId AND " +
        "(LOWER(c.componentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
        "LOWER(c.type) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
        "LOWER(c.subtype) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Component> searchComponentsByProjectAndKeyword(@Param("projectId") Long projectId,
                                                        @Param("keyword") String keyword);
}
