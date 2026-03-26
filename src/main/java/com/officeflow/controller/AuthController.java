package com.officeflow.controller;

import com.officeflow.config.JwtUtils;
import com.officeflow.domain.User;
import com.officeflow.domain.StoredToken;
import com.officeflow.dto.request.LoginRequestDTO;
import com.officeflow.dto.response.JwtResponseDTO;
import com.officeflow.repository.TokenRepository;
import com.officeflow.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final TokenRepository tokenRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        // 1. Autenticazione: controlla se email e password sono corretti
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // 2. Generazione del Token JWT
        String jwt = jwtUtils.generateToken(authentication);
        User userDetails = (User) authentication.getPrincipal();

        // 3. SALVATAGGIO NEL DATABASE (Cruciale per il JwtFilter)
        try {
            StoredToken storedToken = StoredToken.builder()
                    .token(jwt)
                    .userEmail(userDetails.getEmail())
                    .revoked(false)
                    .createdAt(LocalDateTime.now())
                    .build();

            tokenRepository.save(storedToken);
            System.out.println("DEBUG: Token salvato con successo nel DB per l'utente: " + userDetails.getEmail());
        } catch (Exception e) {
            System.err.println("ERRORE CRITICO: Impossibile salvare il token su MongoDB!");
            e.printStackTrace();
            // Opzionale: puoi decidere di bloccare il login se il DB non risponde
            // return ResponseEntity.internalServerError().body("Errore salvataggio sessione");
        }

        // 4. Risposta al client
        return ResponseEntity.ok(new JwtResponseDTO(
                jwt,
                userDetails.getEmail(),
                userDetails.getRole()
        ));
    }
}