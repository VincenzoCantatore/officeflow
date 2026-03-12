package com.officeflow.dto.response;


import lombok.Data;

@Data
public class BookingResponseDTO {
    private String id;
    private String userId;
    private String resourceId;
    private String date;      // allineato con Booking
    private String startHour;
    private String endHour;

}