package com.officeflow.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingRequestDTO {
    private String userId;
    private String resourceId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startHour;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endHour;
}