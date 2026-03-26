package com.officeflow.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.officeflow.domain.enums.BookingStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingResponseDTO {

    @Id
    private String id;

    private String userId;
    private String resourceId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startHour;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endHour;

    private BookingStatus status;
}