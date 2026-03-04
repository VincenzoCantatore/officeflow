package service;

import domain.Resource;
import domain.ResourceType;
import org.springframework.stereotype.Service;
import repository.ResourceRepository;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<Resource> getAllResources(ResourceType type) {
        if (type != null) {
            return resourceRepository.findByType(type);
        }
        return resourceRepository.findAll();
    }

    public Resource createResource(Resource resource) {
        if (resourceRepository.existsByName(resource.getName())) {
            throw new IllegalArgumentException("Esiste già una risorsa con questo nome");
        }
        return resourceRepository.save(resource);
    }

    public void deleteResource(String id) {
        resourceRepository.deleteById(id);
    }
}
