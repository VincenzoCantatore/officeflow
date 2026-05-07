package com.officeflow.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserRequestDTO {

    @NotBlank(message = "Nome obbligatorio")
    private String firstName;

    @NotBlank(message = "Cognome obbligatorio")
    private String lastName;

    @Email(message = "Email non valida")
    @NotBlank(message = "Email obbligatoria")
    private String email;

    @NotBlank(message = "Password obbligatoria")
    private String password;
}
