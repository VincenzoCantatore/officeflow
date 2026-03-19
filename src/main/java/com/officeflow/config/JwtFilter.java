package com.officeflow.config;

import com.officeflow.repository.TokenRepository; // Importa il nuovo repository
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository; // Iniezione del repository dei token

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = parseJwt(request);

            if (jwt != null) {
                // 1. PRIMO CONTROLLO: Esiste nel Database? (Quello che cercavi tu)
                boolean existsInDb = tokenRepository.findByToken(jwt).isPresent();

                // 2. SECONDO CONTROLLO: La firma e la scadenza sono valide?
                if (existsInDb && jwtUtils.validateJwtToken(jwt)) {

                    String email = jwtUtils.getEmailFromJwtToken(jwt);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // OK: L'utente è autorizzato
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    // SE IL TOKEN NON È NEL DB O È SBAGLIATO (es. una sola lettera "X")
                    log.warn("Accesso negato: Token non trovato nel DB o non valido.");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Accesso negato: Token non autorizzato o inesistente\"}");
                    return; // Blocca la catena di filtri qui
                }
            }
        } catch (Exception e) {
            log.error("Errore durante l'autenticazione JWT: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}