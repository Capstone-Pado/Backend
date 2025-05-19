package com.pado.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// [ ] : 어떤 필드를 작성해야할지, 연관 관계는 아마 필요 없을 거 같은데
public class ComponentMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;      // RESOURCE, SERVICE
    private String subtype;   // RESOURCE라면 EC2, S3 , SERVICE라면 MySQL, Spring 등
    private String thumbnail; // 이미지 URL
}
