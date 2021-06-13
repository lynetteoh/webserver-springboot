package com.moneylion.interview.spring.webserver.repository;

import com.moneylion.interview.spring.webserver.model.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface PermissionRepository extends MongoRepository<Permission, String> {
    Optional<Permission> findFirstByEmailAndFeatureName(String email, String featureName);
}
