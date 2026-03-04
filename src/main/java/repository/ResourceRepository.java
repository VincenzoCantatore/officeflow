package repository;

import domain.Resource;
import domain.ResourceType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ResourceRepository extends MongoRepository<Resource,String> {

    List<Resource> findByType(ResourceType type);

    boolean existsByName(String name);
}
