package com.officeflow.dto.response;

import com.officeflow.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor // Genera il costruttore con TUTTI i campi nell'ordine esatto
@NoArgsConstructor  // Necessario per Jackson (per far girare bene il JSON)
public class JwtResponseDTO {
    private String token;
    private String email;
    private UserRole role;
}