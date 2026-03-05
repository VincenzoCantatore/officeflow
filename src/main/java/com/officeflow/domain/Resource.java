package com.officeflow.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Data
@Document(collection = "resources")
public class Resource {

    @Id
    private String id;

    @NotBlank(message = "Il nome della risorsa è obbligatorio")
    private String name;

    @NotBlank(message = "Il tipo della risorsa è obbligatorio")
    private ResourceType type;

    @Min(value = 1, message = "Il piano deve essere maggiore di 0")
    private int floor;

    private List<String> facilities;


}
