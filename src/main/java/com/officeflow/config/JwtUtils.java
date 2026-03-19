package com.officeflow.config;

import com.officeflow.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

    // Questa chiave deve essere lunga almeno 32 caratteri per l'algoritmo HS256
    private final String jwtSecret = "questa_e_una_chiave_segreta_molto_lunga_per_officeflow_2026_security_key";

    // Scadenza: 24 ore (86400000 millisecondi)
    private final int jwtExpirationMs = 86400000;

    // Trasformiamo la stringa in una Chiave sicura
    private final Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes());

    /**
     * Crea il Token partendo dall'utente loggato con successo
     */
    public String generateToken(Authentication authentication) {
        // Recuperiamo l'oggetto User che abbiamo modificato prima (UserDetails)
        User userPrincipal = (User) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userPrincipal.getEmail()) // Identificativo utente
                .setIssuedAt(new Date()) // Data di creazione
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Scadenza
                .signWith(key, SignatureAlgorithm.HS256) // Firma digitale
                .compact();
    }

    /**
     * Legge il token e ci dice qual è l'email dell'utente
     */
    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Verifica se il token è autentico, non manomesso e non scaduto
     */
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Token JWT non valido: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token JWT scaduto: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT non supportato: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("Firma del token non valida: {}", e.getMessage());
        }
        return false;
    }
}