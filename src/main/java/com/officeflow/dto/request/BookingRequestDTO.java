package com.officeflow.dto.request;




public class BookingRequestDTO {
    private String userId;
    private String resourceId;
    private String date;
    private String startHour;
    private String endHour;

    // 1. Costruttore vuoto (essenziale per Spring)
    public BookingRequestDTO() {
    }

    // 2. Costruttore con tutti i campi (comodo per i test)
    public BookingRequestDTO(String userId, String resourceId, String date, String startHour, String endHour) {
        this.userId = userId;
        this.resourceId = resourceId;
        this.date = date;
        this.startHour = startHour;
        this.endHour = endHour;
    }

    // 3. GETTER (permettono al Mapper di leggere i dati)
    public String getUserId() { return userId; }
    public String getResourceId() { return resourceId; }
    public String getDate() { return date; }
    public String getStartHour() { return startHour; }
    public String getEndHour() { return endHour; }

    // 4. SETTER (permettono a Spring di riempire il DTO dal JSON)
    public void setUserId(String userId) { this.userId = userId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    public void setDate(String date) { this.date = date; }
    public void setStartHour(String startHour) { this.startHour = startHour; }
    public void setEndHour(String endHour) { this.endHour = endHour; }

    // 5. toString (per il log: System.out.println("Booking creato: " + response))
    @Override
    public String toString() {
        return "BookingRequestDTO{" +
                "userId='" + userId + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", date='" + date + '\'' +
                ", startHour='" + startHour + '\'' +
                ", endHour='" + endHour + '\'' +
                '}';
    }
}