package domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "resources")
public class Resource {

    @Id
    private String id;

    @NotBlank(message = "Il nome della risorsa è obbligatorio")
    private String name;

    @NotNull(message = "Il tipo di risorsa è obbligatorio")
    private ResourceType type;

    @Min(value = 1, message = "Il piano deve essere maggiore di 0")
    private int floor;

    private List<String> facilities;

    // getter e setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public ResourceType getType() { return type; }
    public void setType(ResourceType type) { this.type = type; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }

    public List<String> getFacilities() { return facilities; }
    public void setFacilities(List<String> facilities) { this.facilities = facilities; }
}
