package com.officeflow.dto.request;


import lombok.Data;

@Data
public class BookingRequestDTO {
    private String userId;
    private String resourceId;
    private String date;
    private String startHour;
    private String endHour;


}