package com.officeflow.service;

import com.officeflow.domain.Resource;
import com.officeflow.domain.ResourceType;
import org.springframework.stereotype.Service;
import com.officeflow.repository.ResourceRepository;

import java.util.List;

@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public List<Resource> getAllResources() {
        return resourceRepository.findAll();
    }

    public Resource createResource(Resource resource) {
        if (resourceRepository.existsByName(resource.getName())) {
            throw new IllegalArgumentException("Esiste già una risorsa registrata con questo nome");
        }
        return resourceRepository.save(resource);
    }

    public void deleteResource(String id) {
        resourceRepository.deleteById(id);
    }
}
