package com.officeflow.domain;

import com.officeflow.domain.enums.UserRoleEnum;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Document(collection = "users")
public class User implements UserDetails { // Fondamentale per Spring Security

    @Id
    private String id;

    @NotBlank(message = "Nome obbligatorio")
    private String firstName;

    @NotBlank(message = "Cognome obbligatorio")
    private String lastName;

    @Email(message = "Email non valida")
    @NotBlank(message = "Email obbligatoria")
    private String email;

    @NotBlank(message = "Password obbligatoria")
    private String password; // Aggiunta per il login

    private UserRoleEnum role; // Es. "USER" o "ADMIN"

    //  METODI DI SPRING SECURITY
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Trasforma la stringa role in un oggetto Authority che Spring capisce
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getUsername() {
        return this.email; // Usiamo l'email come username per il login
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; }

    @Override
    public boolean isAccountNonLocked() {
        return true; }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; }

    @Override
    public boolean isEnabled() {
        return true; }
}