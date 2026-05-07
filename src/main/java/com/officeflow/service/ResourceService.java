package com.officeflow.service;

import com.officeflow.domain.Resource;
import com.officeflow.exception.ResourceNotFoundException;
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
        if (!resourceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Risorsa non trovata con ID: " + id);
        }

        resourceRepository.deleteById(id);
    }
}
