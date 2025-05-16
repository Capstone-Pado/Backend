package com.pado.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pado.backend.domain.Credential;
import com.pado.backend.domain.User;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long>{
    public List<Credential> findByUser(User user);
    // public List<Credential> findByCredentialType(String credentialType);
    // TODO 쿼리로 메서드 명 변경 가능 
    // 수정된 쿼리
    // @Query("SELECT c FROM Credential c WHERE c.credentialType = :credentialType")
    public List<Credential> findByCredentialType(String credentialType);
}
