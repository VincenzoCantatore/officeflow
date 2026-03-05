package com.officeflow.repository;

import com.officeflow.domain.Resource;
import com.officeflow.domain.ResourceType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ResourceRepository extends MongoRepository<Resource,String> {

    List<Resource> findByType(ResourceType type);
    boolean existsByName(String name);
}
