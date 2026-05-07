package com.officeflow.config;

import com.officeflow.domain.StoredToken;
import com.officeflow.repository.TokenRepository;
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
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Estraiamo il token dall'header
        String jwt = parseJwt(request);

        // Se non c'è il token, passiamo oltre.
        // Sarà la SecurityConfig a bloccare l'accesso se la rotta è protetta.
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Controllo incrociato: Esiste nel DB? E la firma è valida?
            StoredToken storedToken = tokenRepository.findByToken(jwt).orElse(null);
            boolean isActiveInDb = storedToken != null && !storedToken.isRevoked();
            boolean isValidSignature = jwtUtils.validateJwtToken(jwt);

            if (isActiveInDb && isValidSignature) {
                // Token perfetto: procediamo con l'autenticazione
                String email = jwtUtils.getEmailFromJwtToken(jwt);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Vai al prossimo filtro/controller
                filterChain.doFilter(request, response);
            } else {
                // TOKEN RIFIUTATO: Qui blocchiamo la catena
                log.warn("Tentativo di accesso fallito: token assente, revocato o firma non valida");
                sendUnauthorizedResponse(response, "Token non autorizzato, revocato o inesistente nel database");
                // IMPORTANTE: Nessun filterChain.doFilter() qui, la richiesta muore!
            }
        } catch (Exception e) {
            log.error("Errore critico nella validazione del token: {}", e.getMessage());
            sendUnauthorizedResponse(response, "Token malformato o errore di sistema");
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"status\": 401, \"error\": \"Unauthorized\", \"message\": \"" + message + "\"}");
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
