package com.officeflow.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Objects;

@Document(collection = "resources")
public class Resource {

    @Id
    private String id;

    @NotBlank(message = "Il nome della risorsa è obbligatorio")
    private String name;

    private ResourceType type;

    @Min(value = 1, message = "Il piano deve essere maggiore di 0")
    private int floor;

    private List<String> facilities;

    // 1. Costruttore vuoto (indispensabile per MongoDB)
    public Resource() {
    }

    // 2. Getter e Setter (indispensabili per il Mapper)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public List<String> getFacilities() {
        return facilities;
    }

    public void setFacilities(List<String> facilities) {
        this.facilities = facilities;
    }

    // 3. toString (fondamentale per i tuoi log)
    @Override
    public String toString() {
        return "Resource{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", floor=" + floor +
                ", facilities=" + facilities +
                '}';
    }
}