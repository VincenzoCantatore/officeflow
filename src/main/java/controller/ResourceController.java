package controller;

import domain.Resource;
import domain.ResourceType;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import service.ResourceService;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @GetMapping
    public List<Resource> getResources(@RequestParam(required = false) ResourceType type) {
        return resourceService.getAllResources(type);
    }

    @PostMapping
    public Resource createResource(@Valid @RequestBody Resource resource) {
        return resourceService.createResource(resource);
    }

    @DeleteMapping("/{id}")
    public void deleteResource(@PathVariable String id) {
        resourceService.deleteResource(id);
    }
}