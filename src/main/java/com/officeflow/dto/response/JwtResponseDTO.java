package com.officeflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // Genera il costruttore con TUTTI i campi nell'ordine esatto
@NoArgsConstructor  // Necessario per Jackson (per far girare bene il JSON)
public class JwtResponseDTO {
    private String token;
    private String email;
    private String role;
}