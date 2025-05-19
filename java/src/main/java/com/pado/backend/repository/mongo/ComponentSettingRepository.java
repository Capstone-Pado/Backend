package com.pado.backend.repository.mongo;

import com.pado.backend.domain.mongo.ComponentSettingDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ComponentSettingRepository extends MongoRepository<ComponentSettingDocument, String> {

    // componentId 기준으로 설정 조회
    Optional<ComponentSettingDocument> findByComponentId(Long componentId);
}
