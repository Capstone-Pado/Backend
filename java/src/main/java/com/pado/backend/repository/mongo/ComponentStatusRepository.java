package com.pado.backend.repository.mongo;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.pado.backend.domain.mongo.ComponentStatusDocument;

@Repository
public interface ComponentStatusRepository extends MongoRepository<ComponentStatusDocument, String> {

    // 특정 컴포넌트의 최신 상태 1개 조회 (가장 최근 timestamp 기준)
    Optional<ComponentStatusDocument> findTopByComponentIdOrderByTimestampDesc(String componentId);
}
