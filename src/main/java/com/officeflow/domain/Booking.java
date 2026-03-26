package com.officeflow.domain;

import com.officeflow.domain.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;
import java.time.LocalTime;

@Document(collection = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    private String id;
    private String userId;
    private String resourceId;
    private LocalDate date;
    private LocalTime startHour;
    private LocalTime endHour;
    private BookingStatus status;
    private LocalTime adesso ;
}