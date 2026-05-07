package com.officeflow.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingRequestDTO {
    @NotBlank(message = "ID utente obbligatorio")
    private String userId;

    @NotBlank(message = "ID risorsa obbligatorio")
    private String resourceId;

    @NotNull(message = "Data obbligatoria")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "Ora di inizio obbligatoria")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startHour;

    @NotNull(message = "Ora di fine obbligatoria")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endHour;
}
