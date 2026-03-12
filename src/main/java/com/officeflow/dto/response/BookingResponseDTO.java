package com.officeflow.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Objects;

public class BookingResponseDTO {
    private String id;
    private String userId;
    private String resourceId;
    private String date;      // allineato con Booking
    private String startHour;
    private String endHour;

    // Costruttore vuoto (necessario per la deserializzazione)
    public BookingResponseDTO() {
    }

    // Getter e Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    public String getEndHour() {
        return endHour;
    }

    public void setEndHour(String endHour) {
        this.endHour = endHour;
    }

    // toString per visualizzare i dati nel System.out.println del Controller
    @Override
    public String toString() {
        return "BookingResponseDTO{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", date='" + date + '\'' +
                ", startHour='" + startHour + '\'' +
                ", endHour='" + endHour + '\'' +
                '}';
    }
}