package com.officeflow.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Objects;

@Data
@Document(collection = "booking")
public class Booking {

    @Id
    private String id;
    private String userId;
    private String resourceId;
    private String date;
    private String startHour;
    private String endHour;
}