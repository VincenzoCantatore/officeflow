package com.officeflow.config;

import com.officeflow.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Slf4j
public class JwtUtils {

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

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
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Firma digitale
                .compact();
    }

    /**
     * Legge il token e ci dice qual è l'email dell'utente
     */
    public String getEmailFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
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
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
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

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
